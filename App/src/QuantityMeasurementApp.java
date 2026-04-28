import java.util.Objects;

/**
 * UC10: The Core Contract for all Measurement Units.
 * This interface allows the Quantity class to be generic across categories.
 */
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

/**
 * UC8-10: Standalone Enums implementing IMeasurable.
 * Centralizes conversion factors and math for each category.
 */
enum LengthUnit implements IMeasurable {
    YARDS(36.0), FEET(12.0), INCHES(1.0), CENTIMETERS(0.393701);

    private final double factor; // Factor relative to INCHES (base)
    LengthUnit(double factor) { this.factor = factor; }

    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0), GRAM(0.001), POUND(0.453592);

    private final double factor; // Factor relative to KILOGRAM (base)
    WeightUnit(double factor) { this.factor = factor; }

    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

/**
 * UC10: Generic Value Object.
 * Handles comparison, conversion, and arithmetic for any IMeasurable unit.
 */
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    // UC5: Conversion logic
    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(baseValue), targetUnit);
    }

    // UC6 & UC7: Addition with result in specific target unit
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (other == null || targetUnit == null) throw new IllegalArgumentException("Params cannot be null");
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    // UC1-UC4: Equality with Category Safety
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Quantity<?> that = (Quantity<?>) obj;

        // Strict Category Check: Length cannot equal Weight
        if (this.unit.getClass() != that.unit.getClass()) return false;

        // Compare normalized base values with epsilon tolerance
        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                ((IMeasurable)that.unit).convertToBaseUnit(that.value)) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.getClass(), unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return String.format("%.3f %s", value, unit.getUnitName());
    }
}

/**
 * Main Application: Orchestrates demonstrations using Generic methods.
 */
public class QuantityMeasurementApp {

    // Generic demonstration method for any measurement category
    public static void demonstrate(String label, Quantity<?> q1, Quantity<?> q2) {
        System.out.println("[" + label + "]");
        System.out.println("  Values: " + q1 + " and " + q2);
        System.out.println("  Equal?  " + q1.equals(q2));
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== QUANTITY MEASUREMENT SYSTEM (UC1-UC10) ===\n");

        // 1. Length Comparison (Metric & Imperial)
        Quantity<LengthUnit> oneFoot = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> twelveInches = new Quantity<>(12.0, LengthUnit.INCHES);
        demonstrate("LENGTH EQUALITY", oneFoot, twelveInches);

        // 2. Weight Comparison (Metric & Imperial)
        Quantity<WeightUnit> oneKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> poundApprox = new Quantity<>(2.20462, WeightUnit.POUND);
        demonstrate("WEIGHT EQUALITY", oneKg, poundApprox);

        // 3. Unit Conversion
        Quantity<LengthUnit> yard = new Quantity<>(1.0, LengthUnit.YARDS);
        System.out.println("[CONVERSION] 1 Yard to Inches: " + yard.convertTo(LengthUnit.INCHES));

        // 4. Cross-Unit Addition
        Quantity<WeightUnit> halfKg = new Quantity<>(0.5, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(500, WeightUnit.GRAM);
        Quantity<WeightUnit> total = halfKg.add(grams, WeightUnit.KILOGRAM);
        System.out.println("[ADDITION] 0.5kg + 500g = " + total);

        // 5. Category Safety (UC10 Proof)
        System.out.println("\n[SAFETY] Comparing 1 Foot to 1 Kilogram...");
        System.out.println("  Result: " + oneFoot.equals(oneKg));
    }
}