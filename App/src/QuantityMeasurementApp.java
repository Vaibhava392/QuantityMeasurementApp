import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

/**
 * UC10-13: The Core Interface for all Measurement Categories.
 * Provides a contract for unit normalization.
 */
interface IMeasurable {
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

/**
 * UC11-13: Volume Unit Definitions.
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
 * UC8-13: Length Unit Definitions.
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
 * UC9-13: Weight Unit Definitions.
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
 * UC13: Refactored Quantity Class.
 * Centralizes validation, arithmetic dispatch, and result formatting.
 */
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    /**
     * UC13: Internal Strategy for Arithmetic Operations.
     * Uses Lambda expressions to define logic for each operation type.
     */
    private enum ArithmeticOperation {
        ADD((a, b) -> a + b),
        SUBTRACT((a, b) -> a - b),
        DIVIDE((a, b) -> {
            if (b == 0) throw new ArithmeticException("Division by zero quantity");
            return a / b;
        });

        private final DoubleBinaryOperator operator;
        ArithmeticOperation(DoubleBinaryOperator operator) { this.operator = operator; }
        public double compute(double v1, double v2) { return operator.applyAsDouble(v1, v2); }
    }

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Value must be finite");
        this.value = value;
        this.unit = unit;
    }

    // --- Public API ---

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        return executeQuantityOperation(other, targetUnit, ArithmeticOperation.ADD);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        return executeQuantityOperation(other, targetUnit, ArithmeticOperation.SUBTRACT);
    }

    public double divide(Quantity<U> other) {
        validateArithmeticOperands(other, null, false);
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    // --- UC13: Centralized Core Logic (Helpers) ---

    /**
     * Centralized helper to perform the full arithmetic flow for Quantity results.
     */
    private Quantity<U> executeQuantityOperation(Quantity<U> other, U targetUnit, ArithmeticOperation op) {
        validateArithmeticOperands(other, targetUnit, true);
        double resultInBase = performBaseArithmetic(other, op);
        double convertedValue = targetUnit.convertFromBaseUnit(resultInBase);
        return new Quantity<>(roundToTwoDecimals(convertedValue), targetUnit);
    }

    /**
     * Centralized helper to normalize values to base units and perform computation.
     */
    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation op) {
        double v1 = this.unit.convertToBaseUnit(this.value);
        double v2 = other.unit.convertToBaseUnit(other.value);
        return op.compute(v1, v2);
    }

    /**
     * Centralized validation helper to enforce consistency.
     */
    private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetUnitRequired) {
        if (other == null) throw new IllegalArgumentException("Operand cannot be null");
        if (this.unit.getClass() != other.unit.getClass()) {
            throw new IllegalArgumentException("Category mismatch: Cannot perform arithmetic between " +
                    this.unit.getClass().getSimpleName() + " and " + other.unit.getClass().getSimpleName());
        }
        if (targetUnitRequired && targetUnit == null) {
            throw new IllegalArgumentException("Target unit is required for this operation");
        }
    }

    private double roundToTwoDecimals(double val) {
        return Math.round(val * 100.0) / 100.0;
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

    @Override public int hashCode() { return Objects.hash(unit.getClass(), unit.convertToBaseUnit(value)); }
    @Override public String toString() { return String.format("%.2f %s", value, unit.getUnitName()); }
}

/**
 * UC13: Main Application demonstrating refactored behavior.
 */
public class QuantityMeasurementApp {
    public static void main(String[] args) {
        System.out.println("=== UC13: Centralized Arithmetic Logic (DRY) ===\n");

        // Length Operations
        Quantity<LengthUnit> tenFeet = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> twoFeet = new Quantity<>(2.0, LengthUnit.FEET);

        System.out.println("Addition (10ft + 2ft): " + tenFeet.add(twoFeet));
        System.out.println("Subtraction (10ft - 2ft): " + tenFeet.subtract(twoFeet));
        System.out.println("Division (10ft / 2ft): " + tenFeet.divide(twoFeet));

        // Volume Operations (Cross-unit)
        Quantity<VolumeUnit> oneLitre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> fiveHundredML = new Quantity<>(500.0, VolumeUnit.MILLILITRE);

        System.out.println("\n1.0L - 500mL: " + oneLitre.subtract(fiveHundredML));
        System.out.println("1.0L / 500mL: " + oneLitre.divide(fiveHundredML));

        // Validation Consistency
        try {
            System.out.println("\nAttempting Cross-Category Addition (Length + Volume)...");
            tenFeet.add(new Quantity<>(1.0, VolumeUnit.LITRE));
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Success: " + e.getMessage());
        }
    }
}