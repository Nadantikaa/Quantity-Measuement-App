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

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

public class mesurementapp {

    static class Quantity<U extends IMeasurable> {
        private final double value;
        private final U unit;
        private static final double EPSILON = 1e-6;

        public Quantity(double value, U unit) {
            if (!Double.isFinite(value) || unit == null) throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        public Quantity<U> convertTo(U target) {
            if (target == null) throw new IllegalArgumentException();
            double base = unit.convertToBaseUnit(value);
            double converted = target.convertFromBaseUnit(base);
            return new Quantity<>(converted, target);
        }

        public Quantity<U> add(Quantity<U> other) {
            if (other == null) throw new IllegalArgumentException();
            return add(other, this.unit);
        }

        public Quantity<U> add(Quantity<U> other, U target) {
            if (other == null || target == null) throw new IllegalArgumentException();
            double sum = unit.convertToBaseUnit(value) + other.unit.convertToBaseUnit(other.value);
            return new Quantity<>(target.convertFromBaseUnit(sum), target);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Quantity<?> other = (Quantity<?>) obj;
            if (unit.getClass() != other.unit.getClass()) return false;
            double a = unit.convertToBaseUnit(value);
            double b = other.unit.convertToBaseUnit(other.value);
            return Math.abs(a - b) < EPSILON;
        }

        @Override
        public int hashCode() {
            long bits = Double.doubleToLongBits(unit.convertToBaseUnit(value));
            return (int) (bits ^ (bits >>> 32));
        }

        @Override
        public String toString() {
            return value + " " + unit.getUnitName();
        }
    }

    public static void main(String[] args) {
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCH);
        System.out.println(l1.equals(l2));
        System.out.println(l1.convertTo(LengthUnit.INCH));
        System.out.println(l1.add(l2, LengthUnit.FEET));

        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);
        System.out.println(w1.equals(w2));
        System.out.println(w1.convertTo(WeightUnit.GRAM));
        System.out.println(w1.add(w2, WeightUnit.KILOGRAM));
    }
}