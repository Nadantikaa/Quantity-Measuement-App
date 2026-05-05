interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCH(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double factor;

    LengthUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) { this.factor = factor; }

    public double getConversionFactor() { return factor; }
    public double convertToBaseUnit(double value) { return value * factor; }
    public double convertFromBaseUnit(double baseValue) { return baseValue / factor; }
    public String getUnitName() { return name(); }
}

public class mesurementapp {

    static class Quantity<U extends IMeasurable> {
        private final double value;
        private final U unit;
        private static final double EPSILON = 1e-6;

        private enum ArithmeticOperation {
            ADD((a, b) -> a + b),
            SUBTRACT((a, b) -> a - b),
            DIVIDE((a, b) -> {
                if (b == 0) throw new ArithmeticException();
                return a / b;
            });

            private final java.util.function.DoubleBinaryOperator op;

            ArithmeticOperation(java.util.function.DoubleBinaryOperator op) {
                this.op = op;
            }

            double compute(double a, double b) {
                return op.applyAsDouble(a, b);
            }
        }

        public Quantity(double value, U unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        private void validate(Quantity<U> other, U target, boolean needTarget) {
            if (other == null) throw new IllegalArgumentException();
            if (!Double.isFinite(other.value)) throw new IllegalArgumentException();
            if (unit.getClass() != other.unit.getClass()) throw new IllegalArgumentException();
            if (needTarget && target == null) throw new IllegalArgumentException();
        }

        private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation op) {
            validate(other, null, false);
            return op.compute(this.toBase(), other.toBase());
        }

        private double round(double v) {
            return Math.round(v * 100.0) / 100.0;
        }

        public Quantity<U> convertTo(U target) {
            if (target == null) throw new IllegalArgumentException();
            return new Quantity<>(target.convertFromBaseUnit(toBase()), target);
        }

        public Quantity<U> add(Quantity<U> other) {
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U target) {
            validate(other, target, true);
            double result = performBaseArithmetic(other, ArithmeticOperation.ADD);
            return new Quantity<>(round(target.convertFromBaseUnit(result)), target);
        }

        public Quantity<U> subtract(Quantity<U> other) {
            return subtract(other, this.unit);
        }

        public Quantity<U> subtract(Quantity<U> other, U target) {
            validate(other, target, true);
            double result = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
            return new Quantity<>(round(target.convertFromBaseUnit(result)), target);
        }

        public double divide(Quantity<U> other) {
            validate(other, null, false);
            return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Quantity<?> other = (Quantity<?>) obj;
            if (unit.getClass() != other.unit.getClass()) return false;
            return Math.abs(this.toBase() - other.unit.convertToBaseUnit(other.value)) < EPSILON;
        }

        @Override
        public int hashCode() {
            long bits = Double.doubleToLongBits(toBase());
            return (int) (bits ^ (bits >>> 32));
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }
    }

    public static void main(String[] args) {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println(a.add(b));
        System.out.println(a.subtract(b));
        System.out.println(a.divide(new Quantity<>(2.0, LengthUnit.FEET)));
    }
}