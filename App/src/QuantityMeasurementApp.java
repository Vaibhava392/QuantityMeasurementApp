import java.util.Objects;

/**
 * UC10-12: The Core Interface for all Measurement Categories.
 */
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

/**
 * UC11: Volume Units implementation.
 */
enum VolumeUnit implements IMeasurable {
    LITRE(1.0), MILLILITRE(0.001), GALLON(3.78541);
    private final double factor;
    VolumeUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

/**
 * UC8: Length Units implementation.
 */
enum LengthUnit implements IMeasurable {
    FEET(12.0), INCHES(1.0), YARDS(36.0), CENTIMETERS(0.3937);
    private final double factor;
    LengthUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

/**
 * UC9: Weight Units implementation.
 */
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0), GRAM(0.001), POUND(0.4535);
    private final double factor;
    WeightUnit(double factor) { this.factor = factor; }
    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

/**
 * UC10-12: Generic Quantity Class.
 * Handles all logic for comparison, conversion, and arithmetic.
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

    // UC5: Unit Conversion
    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(baseValue), targetUnit);
    }

    // UC6-7: Addition
    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateCategory(other);
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return createQuantityWithRounding(sumInBase, targetUnit);
    }

    // UC12: Subtraction
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateCategory(other);
        double diffInBase = this.unit.convertToBaseUnit(this.value) -
                other.unit.convertToBaseUnit(other.value);
        return createQuantityWithRounding(diffInBase, targetUnit);
    }

    // UC12: Division (Dimensionless result)
    public double divide(Quantity<U> other) {
        validateCategory(other);
        double divisorBase = other.unit.convertToBaseUnit(other.value);
        if (divisorBase == 0) throw new ArithmeticException("Cannot divide by zero quantity");
        return this.unit.convertToBaseUnit(this.value) / divisorBase;
    }

    // Shared internal rounding logic for Quantity creation
    private Quantity<U> createQuantityWithRounding(double baseValue, U targetUnit) {
        double converted = targetUnit.convertFromBaseUnit(baseValue);
        double rounded = Math.round(converted * 100.0) / 100.0;
        return new Quantity<>(rounded, targetUnit);
    }

    private void validateCategory(Quantity<U> other) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException("Category mismatch: " +
                    this.unit.getClass().getSimpleName() + " vs " + other.unit.getClass().getSimpleName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity<?> that = (Quantity<?>) obj;
        if (this.unit.getClass() != that.unit.getClass()) return false;

        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                ((IMeasurable)that.unit).convertToBaseUnit(that.value)) < 0.001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.getClass(), unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}

/**
 * Main Application for Demonstration
 */
public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("--- UC12: Full Arithmetic & Multi-Category System ---\n");

        // 1. Subtraction Demo (Length)
        Quantity<LengthUnit> tenFeet = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> sixInches = new Quantity<>(6.0, LengthUnit.INCHES);
        System.out.println("Subtraction: " + tenFeet + " - " + sixInches + " = " + tenFeet.subtract(sixInches));

        // 2. Division Demo (Weight)
        Quantity<WeightUnit> oneKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> twoHundredGrams = new Quantity<>(200.0, WeightUnit.GRAM);
        System.out.println("Division: " + oneKg + " / " + twoHundredGrams + " = " + oneKg.divide(twoHundredGrams));

        // 3. Volume Addition Demo
        Quantity<VolumeUnit> oneGallon = new Quantity<>(1.0, VolumeUnit.GALLON);
        Quantity<VolumeUnit> oneLitre = new Quantity<>(1.0, VolumeUnit.LITRE);
        System.out.println("Addition: " + oneGallon + " + " + oneLitre + " = " + oneGallon.add(oneLitre, VolumeUnit.LITRE));

        // 4. Safety Demo (Category Protection)
        try {
            System.out.println("\nAttempting to subtract Weight from Length...");
            tenFeet.subtract(new Quantity(1.0, WeightUnit.KILOGRAM));
        } catch (IllegalArgumentException e) {
            System.err.println("Caught Expected Error: " + e.getMessage());
        }
    }
}