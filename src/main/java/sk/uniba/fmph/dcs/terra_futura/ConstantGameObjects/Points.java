package sk.uniba.fmph.dcs.terra_futura.ConstantGameObjects;

public record Points(int value) {
    public Points {
        if (value < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

    public Points addPoints(Points points) {
        return new Points(this.value + points.value);
    }

    public Points multiplyPoints(Points points) {
        return new Points(this.value * points.value);
    }
}