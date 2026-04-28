import java.util.Objects;

/**
 * Standard interface for all measurement categories.
 */
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

/**
 * UC11: Volume Units implementation of IMeasurable.
 */
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;
    VolumeUnit(double factor) { this.factor = factor; }

    @Override public double convertToBaseUnit(double v) { return v * factor; }
    @Override public double convertFromBaseUnit(double b) { return b / factor; }
    @Override public String getUnitName() { return this.name(); }
}

/**
 * Generic Quantity Class (Unchanged from UC10).
 */
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        double baseValue = this.unit.convertToBaseUnit(this.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(baseValue), targetUnit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double sumInBase = this.unit.convertToBaseUnit(this.value) +
                other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(targetUnit.convertFromBaseUnit(sumInBase), targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity<?> that = (Quantity<?>) obj;

        // Category Safety: Ensures Volume != Length != Weight
        if (this.unit.getClass() != that.unit.getClass()) return false;

        return Math.abs(this.unit.convertToBaseUnit(this.value) -
                ((IMeasurable)that.unit).convertToBaseUnit(that.value)) < 0.0001;
    }

    @Override public int hashCode() { return Objects.hash(unit.getClass(), unit.convertToBaseUnit(value)); }
    @Override public String toString() { return String.format("%.3f %s", value, unit.getUnitName()); }
}

public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("=== UC11: Volume Category Integration ===\n");

        // 1. Equality Comparison
        Quantity<VolumeUnit> oneLitre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> thousandML = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        System.out.println("1 Litre == 1000 mL: " + oneLitre.equals(thousandML));

        // 2. Unit Conversion
        Quantity<VolumeUnit> oneGallon = new Quantity<>(1.0, VolumeUnit.GALLON);
        System.out.println("1 Gallon to Litres: " + oneGallon.convertTo(VolumeUnit.LITRE));

        // 3. Addition with Target Unit
        Quantity<VolumeUnit> sum = oneLitre.add(oneGallon, VolumeUnit.LITRE);
        System.out.println("1 Litre + 1 Gallon: " + sum); // Result: ~4.785 Litres

        // 4. Cross-Category Prevention
        // Quantity<LengthUnit> oneFoot = new Quantity<>(1.0, LengthUnit.FEET);
        // System.out.println("1 Litre == 1 Foot: " + oneLitre.equals(oneFoot)); // Returns false
    }
}