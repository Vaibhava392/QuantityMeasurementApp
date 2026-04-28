import java.util.Objects;

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
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
            this.value = value;
            this.unit = unit;
        }

        /**
         * Instance method to add another length to this one.
         * The result unit is the unit of the first operand (this).
         */
        public QuantityLength add(QuantityLength other) {
            if (other == null) throw new IllegalArgumentException("Second operand cannot be null");

            double sumInBase = this.unit.convertToBase(this.value) +
                    other.unit.convertToBase(other.value);

            double resultValue = this.unit.convertFromBase(sumInBase);
            return new QuantityLength(resultValue, this.unit);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength that = (QuantityLength) obj;
            return Math.abs(this.unit.convertToBase(this.value) -
                    that.unit.convertToBase(that.value)) < 0.0001;
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

    // Static helper for UC6 demonstration
    public static void demonstrateAddition(QuantityLength l1, QuantityLength l2) {
        QuantityLength result = l1.add(l2);
        System.out.println("Input: " + l1 + " + " + l2 + " -> Output: " + result);
    }

    public static void main(String[] args) {
        System.out.println("--- UC6: Addition of Length Units ---");

        // Feet and Inches
        demonstrateAddition(new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCH));

        // Inches and Feet (Note: result unit follows the first operand)
        demonstrateAddition(new QuantityLength(12.0, LengthUnit.INCH),
                new QuantityLength(1.0, LengthUnit.FEET));

        // Yards and Feet
        demonstrateAddition(new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET));

        // Centimeters and Inches
        demonstrateAddition(new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCH));

        // Identity Property (Adding Zero)
        demonstrateAddition(new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.INCH));
    }
}