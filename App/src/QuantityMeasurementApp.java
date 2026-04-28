public class QuantityMeasurementApp {

    public enum LengthUnit {
        FEET(12.0),
        INCH(1.0);

        private final double conversionFactor;

        LengthUnit(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        public double convertToBase(double value) {
            return value * this.conversionFactor;
        }
    }

    public static class Quantity {
        private final double value;
        private final LengthUnit unit;

        public Quantity(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Quantity that = (Quantity) obj;
            double value1 = this.unit.convertToBase(this.value);
            double value2 = that.unit.convertToBase(that.value);

            return Double.compare(value1, value2) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(unit.convertToBase(value));
        }
    }

    public static void main(String[] args) {
        Quantity oneFoot = new Quantity(1.0, LengthUnit.FEET);
        Quantity twelveInches = new Quantity(12.0, LengthUnit.INCH);
        Quantity oneInch = new Quantity(1.0, LengthUnit.INCH);
        Quantity anotherOneInch = new Quantity(1.0, LengthUnit.INCH);

        System.out.println("Input: Quantity(1.0, \"feet\") and Quantity(12.0, \"inches\")");
        System.out.println("Output: Equal (" + oneFoot.equals(twelveInches) + ")");

        System.out.println("Input: Quantity(1.0, \"inch\") and Quantity(1.0, \"inch\")");
        System.out.println("Output: Equal (" + oneInch.equals(anotherOneInch) + ")");
    }
}