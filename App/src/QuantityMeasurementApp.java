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
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Quantity that = (Quantity) obj;
            double value1 = this.unit.convertToBase(this.value);
            double value2 = that.unit.convertToBase(that.value);

            // Using a small epsilon for floating-point comparison with CM
            return Math.abs(value1 - value2) < 0.00001;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(unit.convertToBase(value));
        }
    }

    public static void main(String[] args) {
        // UC4 Test Cases
        Quantity oneYard = new Quantity(1.0, LengthUnit.YARDS);
        Quantity threeFeet = new Quantity(3.0, LengthUnit.FEET);
        Quantity thirtySixInches = new Quantity(36.0, LengthUnit.INCH);
        Quantity oneCm = new Quantity(1.0, LengthUnit.CENTIMETERS);
        Quantity inchValueForCm = new Quantity(0.393701, LengthUnit.INCH);

        System.out.println("--- Yard Comparisons ---");
        System.out.println("1 Yard == 3 Feet: " + oneYard.equals(threeFeet));
        System.out.println("1 Yard == 36 Inches: " + oneYard.equals(thirtySixInches));

        System.out.println("\n--- Centimeter Comparisons ---");
        System.out.println("1 CM == 0.393701 Inches: " + oneCm.equals(inchValueForCm));

        System.out.println("\n--- Cross-Unit Scalability ---");
        Quantity twoYards = new Quantity(2.0, LengthUnit.YARDS);
        Quantity sixFeet = new Quantity(6.0, LengthUnit.FEET);
        System.out.println("2 Yards == 6 Feet: " + twoYards.equals(sixFeet));
    }
}