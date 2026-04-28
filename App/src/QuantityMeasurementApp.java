import java.util.Objects;

/**
 * UC8: Refactored Standalone Enum
 */
enum LengthUnit {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
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
 * UC8: Simplified QuantityLength class
 * Responsibility: Comparison and Arithmetic only.
 */
class QuantityLength {
    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    public QuantityLength convertTo(LengthUnit targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new QuantityLength(convertedValue, targetUnit);
    }

    public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
        if (other == null || targetUnit == null) throw new IllegalArgumentException("Parameters cannot be null");
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new QuantityLength(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityLength that = (QuantityLength) obj;
        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                that.unit.convertToBaseUnit(that.value)) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit);
    }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("--- UC8: Refactored Architecture ---");

        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);

        // Delegation: QuantityLength calls unit methods internally
        System.out.println("1 Feet to Inches: " + oneFoot.convertTo(LengthUnit.INCHES));

        // Cross-unit equality via the refactored enum
        QuantityLength thirtySixInches = new QuantityLength(36.0, LengthUnit.INCHES);
        QuantityLength oneYard = new QuantityLength(1.0, LengthUnit.YARDS);
        System.out.println("36 Inches == 1 Yard: " + thirtySixInches.equals(oneYard));

        // Testing Enum specific methods
        System.out.println("12 Inches to Base (Feet): " + LengthUnit.INCHES.convertToBaseUnit(12.0));
    }
}