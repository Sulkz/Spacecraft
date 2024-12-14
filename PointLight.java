import com.jogamp.opengl.math.Vec3f;
import gmaths.Vec3;

import java.util.Vector;

public class PointLight {
    private Vec3f position, color;
    private float intensity, linear, exponent, constant;

    public PointLight(Vec3f position, Vec3f color, float intensity, float linear, float exponent, float constant) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.linear = linear;
        this.exponent = exponent;
        this.constant = constant;
    }

    public PointLight(Vec3f position, Vec3f color, float intensity ) {
        this(color, position, intensity,0, 0, 1);
    }

    public Vec3f getPosition() {
        return position;
    }

    public void setPosition(Vec3f position) {
        this.position = position;
    }

    public Vec3f getColor() {
        return color;
    }

    public void setColor(Vec3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }
}
