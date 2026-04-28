import java.util.Objects;

/**
 * UC5: QuantityMeasurementApp
 * Provides explicit conversion operations between length units.
 */
public class QuantityMeasurementApp {

    public enum LengthUnit {
        YARDS(36.0),
        FEET(12.0),
        INCH(1.0),
        CENTIMETERS(0.393701);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double convertToBase(double value) {
            return value * this.conversionFactor;
        }

        public double convertFromBase(double baseValue) {
            return baseValue / this.conversionFactor;
        }
    }

    public static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be a finite number");
            this.value = value;
            this.unit = unit;
        }

        /**
         * Instance method to convert this length to a target unit.
         * Returns a new instance (Immutability).
         */
        public QuantityLength convertTo(LengthUnit targetUnit) {
            double convertedValue = convert(this.value, this.unit, targetUnit);
            return new QuantityLength(convertedValue, targetUnit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength that = (QuantityLength) obj;
            return Math.abs(this.unit.convertToBase(this.value) -
                    that.unit.convertToBase(that.value)) < 0.00001;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit.convertToBase(value));
        }

        @Override
        public String toString() {
            return String.format("%.2f %s", value, unit);
        }
    }

    // --- Static API Methods ---

    /**
     * Core conversion logic: (Value * SourceFactor) / TargetFactor
     */
    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (source == null || target == null) throw new IllegalArgumentException("Units cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");

        double baseValue = source.convertToBase(value);
        return target.convertFromBase(baseValue);
    }

    // Overloaded method 1: Raw values
    public static void demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {
        double result = convert(value, from, to);
        System.out.printf("Input: convert(%.1f, %s, %s) -> Output: %.4f\n", value, from, to, result);
    }

    // Overloaded method 2: Existing object
    public static void demonstrateLengthConversion(QuantityLength length, LengthUnit to) {
        QuantityLength converted = length.convertTo(to);
        System.out.println("Object Conversion: " + length + " converted to " + converted);
    }

    public static void main(String[] args) {
        System.out.println("--- UC5: Unit Conversion API ---");

        // Basic Conversions
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCH);
        demonstrateLengthConversion(3.0, LengthUnit.YARDS, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCH, LengthUnit.YARDS);

        // Precision/CM Conversion
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETERS, LengthUnit.INCH);

        // Instance-based conversion (Method Overloading)
        QuantityLength myLength = new QuantityLength(1.0, LengthUnit.YARDS);
        demonstrateLengthConversion(myLength, LengthUnit.INCH);

        // Validation Test
        try {
            convert(Double.NaN, LengthUnit.FEET, LengthUnit.INCH);
        } catch (IllegalArgumentException e) {
            System.out.println("\nValidation Caught: " + e.getMessage());
        }
    }
}