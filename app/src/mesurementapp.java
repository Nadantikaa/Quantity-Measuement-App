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
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);

        System.out.println(v1.equals(v2));
        System.out.println(v1.convertTo(VolumeUnit.MILLILITRE));
        System.out.println(v1.add(v2, VolumeUnit.LITRE));
        System.out.println(v3.convertTo(VolumeUnit.LITRE));
        System.out.println(v1.add(v3, VolumeUnit.MILLILITRE));
    }
}