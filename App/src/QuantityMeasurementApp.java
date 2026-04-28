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
         * UC6: Implicit addition (defaults to current unit)
         */
        public QuantityLength add(QuantityLength other) {
            return add(other, this.unit);
        }

        /**
         * UC7: Explicit addition with target unit specification
         */
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            if (other == null) throw new IllegalArgumentException("Second operand cannot be null");
            if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");

            return performAddition(this, other, targetUnit);
        }

        /**
         * Private utility method to centralize addition logic (DRY)
         */
        private static QuantityLength performAddition(QuantityLength l1, QuantityLength l2, LengthUnit target) {
            double sumInBase = l1.unit.convertToBase(l1.value) + l2.unit.convertToBase(l2.value);
            double convertedValue = target.convertFromBase(sumInBase);
            return new QuantityLength(convertedValue, target);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            QuantityLength that = (QuantityLength) obj;
            return Math.abs(this.unit.convertToBase(this.value) -
                    that.unit.convertToBase(that.value)) < 0.001;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit.convertToBase(value));
        }

        @Override
        public String toString() {
            return String.format("%.3f %s", value, unit);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- UC7: Addition with Target Unit Specification ---");

        QuantityLength oneFoot = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength twelveInches = new QuantityLength(12.0, LengthUnit.INCH);

        // Result in FEET
        System.out.println("1ft + 12in (Target: FEET)  -> " + oneFoot.add(twelveInches, LengthUnit.FEET));

        // Result in INCHES
        System.out.println("1ft + 12in (Target: INCHES)-> " + oneFoot.add(twelveInches, LengthUnit.INCH));

        // Result in YARDS
        System.out.println("1ft + 12in (Target: YARDS) -> " + oneFoot.add(twelveInches, LengthUnit.YARDS));

        // CM and Inches
        QuantityLength oneCm = new QuantityLength(2.54, LengthUnit.CENTIMETERS);
        QuantityLength oneInch = new QuantityLength(1.0, LengthUnit.INCH);
        System.out.println("2.54cm + 1in (Target: CM)  -> " + oneCm.add(oneInch, LengthUnit.CENTIMETERS));
    }
}