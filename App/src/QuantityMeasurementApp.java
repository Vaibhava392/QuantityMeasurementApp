import java.util.Objects;

/**
 * UC9: Standalone WeightUnit Enum
 */
enum WeightUnit {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * this.conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / this.conversionFactor;
    }
}

/**
 * UC9: QuantityWeight Class
 * Category-specific value object for Weight.
 */
class QuantityWeight {
    private final double value;
    private final WeightUnit unit;

    public QuantityWeight(double value, WeightUnit unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    public QuantityWeight convertTo(WeightUnit targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        return new QuantityWeight(targetUnit.convertFromBaseUnit(baseValue), targetUnit);
    }

    public QuantityWeight add(QuantityWeight other) {
        return add(other, this.unit);
    }

    public QuantityWeight add(QuantityWeight other, WeightUnit targetUnit) {
        if (other == null || targetUnit == null) throw new IllegalArgumentException("Null parameter");
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new QuantityWeight(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        // Strict category check: ensures Weight cannot equal Length
        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityWeight that = (QuantityWeight) obj;
        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                that.unit.convertToBaseUnit(that.value)) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return String.format("%.3f %s", value, unit);
    }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("--- UC9: Weight Measurements ---");

        QuantityWeight oneKg = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight gramEquivalent = new QuantityWeight(1000.0, WeightUnit.GRAM);
        QuantityWeight poundEquivalent = new QuantityWeight(2.20462, WeightUnit.POUND);

        // Equality
        System.out.println("1kg == 1000g: " + oneKg.equals(gramEquivalent));
        System.out.println("1kg == 2.20462lb: " + oneKg.equals(poundEquivalent));

        // Addition
        QuantityWeight sum = oneKg.add(new QuantityWeight(500, WeightUnit.GRAM));
        System.out.println("1kg + 500g: " + sum); // Output: 1.500 KILOGRAM

        // Explicit Target
        QuantityWeight lbSum = oneKg.add(gramEquivalent, WeightUnit.POUND);
        System.out.println("1kg + 1000g in Pounds: " + lbSum); // Output: 4.409 POUND
    }
}