/*
 * Copyright (c) 2009-2017 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package templates.geom;

import com.jme3.export.*;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import java.io.IOException;

/*
 * -- Added *Local methods to cut down on object creation - JS
 */

/**
 * <code>Vector3d</code> defines a Vector for a three double value tuple.
 * <code>Vector3d</code> can represent any three dimensional value, such as a
 * vertex, a normal, etc. Utility methods are also included to aid in
 * mathematical calculations.
 *
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Vector3d implements Savable, Cloneable, java.io.Serializable {

    static final long serialVersionUID = 1;
    
//    private static final Logger logger = Logger.getLogger(Vector3d.class.getName());

    public final static Vector3d ZERO = new Vector3d(0, 0, 0);
    public final static Vector3d NAN = new Vector3d(Double.NaN, Double.NaN, Double.NaN);
    public final static Vector3d UNIT_X = new Vector3d(1, 0, 0);
    public final static Vector3d UNIT_Y = new Vector3d(0, 1, 0);
    public final static Vector3d UNIT_Z = new Vector3d(0, 0, 1);
    public final static Vector3d UNIT_XYZ = new Vector3d(1, 1, 1);
    public final static Vector3d POSITIVE_INFINITY = new Vector3d(
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY,
            Double.POSITIVE_INFINITY);
    public final static Vector3d NEGATIVE_INFINITY = new Vector3d(
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NEGATIVE_INFINITY);

    
	/**
     * the x value of the vector.
     */
    public double x;

    /**
     * the y value of the vector.
     */
    public double y;

    /**
     * the z value of the vector.
     */
    public double z;

    /**
     * Constructor instantiates a new <code>Vector3d</code> with default
     * values of (0,0,0).
     *
     */
    public Vector3d() {
        x = y = z = 0;
    }

    /**
     * Constructor instantiates a new <code>Vector3d</code> with provides
     * values.
     *
     * @param x
     *            the x value of the vector.
     * @param y
     *            the y value of the vector.
     * @param z
     *            the z value of the vector.
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor instantiates a new <code>Vector3d</code> that is a copy
     * of the provided vector
     * @param copy The Vector3d to copy
     */
    public Vector3d(Vector3d copy) {
        this.set(copy);
    }
	
	public Vector3d(Vector3f copy) {
        this.set(copy);
    }

    /**
     * <code>set</code> sets the x,y,z values of the vector based on passed
     * parameters.
     *
     * @param x
     *            the x value of the vector.
     * @param y
     *            the y value of the vector.
     * @param z
     *            the z value of the vector.
     * @return this vector
     */
    public Vector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * <code>set</code> sets the x,y,z values of the vector by copying the
     * supplied vector.
     *
     * @param vect
     *            the vector to copy.
     * @return this vector
     */
    public Vector3d set(Vector3d vect) {
        this.x = vect.x;
        this.y = vect.y;
        this.z = vect.z;
        return this;
    }
	/**
     * <code>set</code> sets the x,y,z values of the vector by copying the
     * supplied vector.
     *
     * @param vect
     *            the vector to copy.
     * @return this vector
     */
    public Vector3d set(Vector3f vect) {
        this.x = vect.x;
        this.y = vect.y;
        this.z = vect.z;
        return this;
    }

    /**
     *
     * <code>add</code> adds a provided vector to this vector creating a
     * resultant vector which is returned. If the provided vector is null, null
     * is returned.
     *
     * @param vec
     *            the vector to add to this.
     * @return the resultant vector.
     */
    public Vector3d add(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        return new Vector3d(x + vec.x, y + vec.y, z + vec.z);
    }

    /**
     *
     * <code>add</code> adds the values of a provided vector storing the
     * values in the supplied vector.
     *
     * @param vec
     *            the vector to add to this
     * @param result
     *            the vector to store the result in
     * @return result returns the supplied result vector.
     */
    public Vector3d add(Vector3d vec, Vector3d result) {
        result.x = x + vec.x;
        result.y = y + vec.y;
        result.z = z + vec.z;
        return result;
    }

    /**
     * <code>addLocal</code> adds a provided vector to this vector internally,
     * and returns a handle to this vector for easy chaining of calls. If the
     * provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to add to this vector.
     * @return this
     */
    public Vector3d addLocal(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }
	public Vector3d addLocal(Vector3f vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    /**
     *
     * <code>add</code> adds the provided values to this vector, creating a
     * new vector that is then returned.
     *
     * @param addX
     *            the x value to add.
     * @param addY
     *            the y value to add.
     * @param addZ
     *            the z value to add.
     * @return the result vector.
     */
    public Vector3d add(double addX, double addY, double addZ) {
        return new Vector3d(x + addX, y + addY, z + addZ);
    }

    /**
     * <code>addLocal</code> adds the provided values to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls.
     *
     * @param addX
     *            value to add to x
     * @param addY
     *            value to add to y
     * @param addZ
     *            value to add to z
     * @return this
     */
    public Vector3d addLocal(double addX, double addY, double addZ) {
        x += addX;
        y += addY;
        z += addZ;
        return this;
    }

    /**
     *
     * <code>scaleAdd</code> multiplies this vector by a scalar then adds the
     * given Vector3d.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @param add
     *            the value to add
     */
    public Vector3d scaleAdd(double scalar, Vector3d add) {
        x = x * scalar + add.x;
        y = y * scalar + add.y;
        z = z * scalar + add.z;
        return this;
    }

    /**
     *
     * <code>scaleAdd</code> multiplies the given vector by a scalar then adds
     * the given vector.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @param mult
     *            the value to multiply the scalar by
     * @param add
     *            the value to add
     */
    public Vector3d scaleAdd(double scalar, Vector3d mult, Vector3d add) {
        this.x = mult.x * scalar + add.x;
        this.y = mult.y * scalar + add.y;
        this.z = mult.z * scalar + add.z;
        return this;
    }

    /**
     *
     * <code>dot</code> calculates the dot product of this vector with a
     * provided vector. If the provided vector is null, 0 is returned.
     *
     * @param vec
     *            the vector to dot with this vector.
     * @return the resultant dot product of this vector and a given vector.
     */
    public double dot(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, 0 returned.");
//            return 0;
//        }
        return x * vec.x + y * vec.y + z * vec.z;
    }
	public double dot(double x2, double y2, double z2) {
		 return x * x2 + y * y2 + z * z2;
	}

    /**
     * <code>cross</code> calculates the cross product of this vector with a
     * parameter vector v.
     *
     * @param v
     *            the vector to take the cross product of with this.
     * @return the cross product vector.
     */
    public Vector3d cross(Vector3d v) {
        return cross(v, null);
    }

    /**
     * <code>cross</code> calculates the cross product of this vector with a
     * parameter vector v.  The result is stored in <code>result</code>
     *
     * @param v
     *            the vector to take the cross product of with this.
     * @param result
     *            the vector to store the cross product result.
     * @return result, after recieving the cross product vector.
     */
    public Vector3d cross(Vector3d v,Vector3d result) {
        return cross(v.x, v.y, v.z, result);
    }

    /**
     * <code>cross</code> calculates the cross product of this vector with a
     * parameter vector v.  The result is stored in <code>result</code>
     *
     * @param otherX
     *            x component of the vector to take the cross product of with this.
     * @param otherY
     *            y component of the vector to take the cross product of with this.
     * @param otherZ
     *            z component of the vector to take the cross product of with this.
     * @param result
     *            the vector to store the cross product result.
     * @return result, after recieving the cross product vector.
     */
    public Vector3d cross(double otherX, double otherY, double otherZ, Vector3d result) {
        if (result == null) result = new Vector3d();
        double resX = ((y * otherZ) - (z * otherY)); 
        double resY = ((z * otherX) - (x * otherZ));
        double resZ = ((x * otherY) - (y * otherX));
        result.set(resX, resY, resZ);
        return result;
    }

    /**
     * <code>crossLocal</code> calculates the cross product of this vector
     * with a parameter vector v.
     *
     * @param v
     *            the vector to take the cross product of with this.
     * @return this.
     */
    public Vector3d crossLocal(Vector3d v) {
        return crossLocal(v.x, v.y, v.z);
    }

    /**
     * <code>crossLocal</code> calculates the cross product of this vector
     * with a parameter vector v.
     *
     * @param otherX
     *            x component of the vector to take the cross product of with this.
     * @param otherY
     *            y component of the vector to take the cross product of with this.
     * @param otherZ
     *            z component of the vector to take the cross product of with this.
     * @return this.
     */
    public Vector3d crossLocal(double otherX, double otherY, double otherZ) {
        double tempx = ( y * otherZ ) - ( z * otherY );
        double tempy = ( z * otherX ) - ( x * otherZ );
        z = (x * otherY) - (y * otherX);
        x = tempx;
        y = tempy;
        return this;
    }

    /**
     * Projects this vector onto another vector
     *
     * @param other The vector to project this vector onto
     * @return A new vector with the projection result
     */
    public Vector3d project(Vector3d other){
        double n = this.dot(other); // A . B
        double d = other.lengthSquared(); // |B|^2
        return new Vector3d(other).multLocal(n/d);
    }

    /**
     * Projects this vector onto another vector, stores the result in this
     * vector
     *
     * @param other The vector to project this vector onto
     * @return This Vector3d, set to the projection result
     */
    public Vector3d projectLocal(Vector3d other){
        double n = this.dot(other); // A . B
        double d = other.lengthSquared(); // |B|^2
        return set(other).multLocal(n/d);
    }
    
    /**
     * Returns true if this vector is a unit vector (length() ~= 1),
     * returns false otherwise.
     * 
     * @return true if this vector is a unit vector (length() ~= 1),
     * or false otherwise.
     */
    public boolean isUnitVector(){
        double len = length();
        return 0.99f < len && len < 1.01f;
    }

    /**
     * <code>length</code> calculates the magnitude of this vector.
     *
     * @return the length or magnitude of the vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * <code>lengthSquared</code> calculates the squared value of the
     * magnitude of the vector.
     *
     * @return the magnitude squared of the vector.
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * <code>distanceSquared</code> calculates the distance squared between
     * this vector and vector v.
     *
     * @param v the second vector to determine the distance squared.
     * @return the distance squared between the two vectors.
     */
    public double distanceSquared(Vector3d v) {
        double dx = x - v.x;
        double dy = y - v.y;
        double dz = z - v.z;
        return (double) (dx * dx + dy * dy + dz * dz);
    }

    /**
     * <code>distance</code> calculates the distance between this vector and
     * vector v.
     *
     * @param v the second vector to determine the distance.
     * @return the distance between the two vectors.
     */
    public double distance(Vector3d v) {
        return Math.sqrt(distanceSquared(v));
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is returned.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @return the new vector.
     */
    public Vector3d mult(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    /**
     *
     * <code>mult</code> multiplies this vector by a scalar. The resultant
     * vector is supplied as the second parameter and returned.
     *
     * @param scalar the scalar to multiply this vector by.
     * @param product the product to store the result in.
     * @return product
     */
    public Vector3d mult(double scalar, Vector3d product) {
        if (null == product) {
            product = new Vector3d();
        }

        product.x = x * scalar;
        product.y = y * scalar;
        product.z = z * scalar;
        return product;
    }

    /**
     * <code>multLocal</code> multiplies this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls.
     *
     * @param scalar
     *            the value to multiply this vector by.
     * @return this
     */
    public Vector3d multLocal(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to mult to this vector.
     * @return this
     */
    public Vector3d multLocal(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies this vector by 3 scalars
     * internally, and returns a handle to this vector for easy chaining of
     * calls.
     *
     * @param x
     * @param y
     * @param z
     * @return this
     */
    public Vector3d multLocal(double x, double y, double z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to mult to this vector.
     * @return this
     */
    public Vector3d mult(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        return mult(vec, new Vector3d());
    }

    /**
     * <code>multLocal</code> multiplies a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to mult to this vector.
     * @param store result vector (null to create a new vector)
     * @return this
     */
    public Vector3d mult(Vector3d vec, Vector3d store) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
//        if (store == null) store = new Vector3d();
        return store.set(x * vec.x, y * vec.y, z * vec.z);
    }


    /**
     * <code>divide</code> divides the values of this vector by a scalar and
     * returns the result. The values of this vector remain untouched.
     *
     * @param scalar
     *            the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector3d divide(double scalar) {
        scalar = 1f/scalar;
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls. Dividing
     * by zero will result in an exception.
     *
     * @param scalar
     *            the value to divides this vector by.
     * @return this
     */
    public Vector3d divideLocal(double scalar) {
        scalar = 1f/scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }


    /**
     * <code>divide</code> divides the values of this vector by a scalar and
     * returns the result. The values of this vector remain untouched.
     *
     * @param scalar
     *            the value to divide this vectors attributes by.
     * @return the result <code>Vector</code>.
     */
    public Vector3d divide(Vector3d scalar) {
        return new Vector3d(x / scalar.x, y / scalar.y, z / scalar.z);
    }

    /**
     * <code>divideLocal</code> divides this vector by a scalar internally,
     * and returns a handle to this vector for easy chaining of calls. Dividing
     * by zero will result in an exception.
     *
     * @param scalar
     *            the value to divides this vector by.
     * @return this
     */
    public Vector3d divideLocal(Vector3d scalar) {
        x /= scalar.x;
        y /= scalar.y;
        z /= scalar.z;
        return this;
    }

    /**
     *
     * <code>negate</code> returns the negative of this vector. All values are
     * negated and set to a new vector.
     *
     * @return the negated vector.
     */
    public Vector3d negate() {
        return new Vector3d(-x, -y, -z);
    }

    /**
     *
     * <code>negateLocal</code> negates the internal values of this vector.
     *
     * @return this.
     */
    public Vector3d negateLocal() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    /**
     *
     * <code>subtract</code> subtracts the values of a given vector from those
     * of this vector creating a new vector object. If the provided vector is
     * null, null is returned.
     *
     * @param vec
     *            the vector to subtract from this vector.
     * @return the result vector.
     */
    public Vector3d subtract(Vector3d vec) {
        return new Vector3d(x - vec.x, y - vec.y, z - vec.z);
    }

    /**
     * <code>subtractLocal</code> subtracts a provided vector to this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls. If the provided vector is null, null is returned.
     *
     * @param vec
     *            the vector to subtract
     * @return this
     */
    public Vector3d subtractLocal(Vector3d vec) {
//        if (null == vec) {
//            logger.warning("Provided vector is null, null returned.");
//            return null;
//        }
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     *
     * <code>subtract</code>
     *
     * @param vec
     *            the vector to subtract from this
     * @param result
     *            the vector to store the result in
     * @return result
     */
    public Vector3d subtract(Vector3d vec, Vector3d result) {
//        if(result == null) {
//            result = new Vector3d();
//        }
        result.x = x - vec.x;
        result.y = y - vec.y;
        result.z = z - vec.z;
        return result;
    }

    /**
     *
     * <code>subtract</code> subtracts the provided values from this vector,
     * creating a new vector that is then returned.
     *
     * @param subtractX
     *            the x value to subtract.
     * @param subtractY
     *            the y value to subtract.
     * @param subtractZ
     *            the z value to subtract.
     * @return the result vector.
     */
    public Vector3d subtract(double subtractX, double subtractY, double subtractZ) {
        return new Vector3d(x - subtractX, y - subtractY, z - subtractZ);
    }

    /**
     * <code>subtractLocal</code> subtracts the provided values from this vector
     * internally, and returns a handle to this vector for easy chaining of
     * calls.
     *
     * @param subtractX
     *            the x value to subtract.
     * @param subtractY
     *            the y value to subtract.
     * @param subtractZ
     *            the z value to subtract.
     * @return this
     */
    public Vector3d subtractLocal(double subtractX, double subtractY, double subtractZ) {
        x -= subtractX;
        y -= subtractY;
        z -= subtractZ;
        return this;
    }

    /**
     * <code>normalize</code> returns the unit vector of this vector.
     *
     * @return unit vector of this vector.
     */
    public Vector3d normalize() {
//        double length = length();
//        if (length != 0) {
//            return divide(length);
//        }
//
//        return divide(1);
        double length = x * x + y * y + z * z;
        if (length != 1 && length != 0){
            length = 1.0 / Math.sqrt(length);
            return new Vector3d(x * length, y * length, z * length);
        }
        return clone();
    }

    /**
     * <code>normalizeLocal</code> makes this vector into a unit vector of
     * itself.
     *
     * @return this.
     */
    public Vector3d normalizeLocal() {
        // NOTE: this implementation is more optimized
        // than the old jme normalize as this method
        // is commonly used.
        double length = x * x + y * y + z * z;
        if (length != 1 && length != 0){
            length = 1.0 / Math.sqrt(length);
            x *= length;
            y *= length;
            z *= length;
        }
        return this;
    }

    /**
     * <code>maxLocal</code> computes the maximum value for each 
     * component in this and <code>other</code> vector. The result is stored
     * in this vector.
     * @param other 
     */
    public Vector3d maxLocal(Vector3d other){
        x = other.x > x ? other.x : x;
        y = other.y > y ? other.y : y;
        z = other.z > z ? other.z : z;
        return this;
    }

    /**
     * <code>minLocal</code> computes the minimum value for each
     * component in this and <code>other</code> vector. The result is stored
     * in this vector.
     * @param other
     */
    public Vector3d minLocal(Vector3d other){
        x = other.x < x ? other.x : x;
        y = other.y < y ? other.y : y;
        z = other.z < z ? other.z : z;
        return this;
    }

    /**
     * <code>zero</code> resets this vector's data to zero internally.
     */
    public Vector3d zero() {
        x = y = z = 0;
        return this;
    }

    /**
     * <code>angleBetween</code> returns (in radians) the angle between two vectors.
     * It is assumed that both this vector and the given vector are unit vectors (iow, normalized).
     * 
     * @param otherVector a unit vector to find the angle against
     * @return the angle in radians.
     */
    public double angleBetween(Vector3d otherVector) {
        double dotProduct = dot(otherVector);
        double angle = Math.acos(dotProduct);
        return angle;
    }
    
    /**
     * Sets this vector to the interpolation by changeAmnt from this to the finalVec
     * this=(1-changeAmnt)*this + changeAmnt * finalVec
     * @param finalVec The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from this towards finalVec
     */
    public Vector3d interpolateLocal(Vector3d finalVec, double changeAmnt) {
        this.x=(1-changeAmnt)*this.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*this.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*this.z + changeAmnt*finalVec.z;
        return this;
    }

    /**
     * Sets this vector to the interpolation by changeAmnt from beginVec to finalVec
     * this=(1-changeAmnt)*beginVec + changeAmnt * finalVec
     * @param beginVec the beging vector (changeAmnt=0)
     * @param finalVec The final vector to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginVec towards finalVec
     */
    public Vector3d interpolateLocal(Vector3d beginVec,Vector3d finalVec, double changeAmnt) {
        this.x=(1-changeAmnt)*beginVec.x + changeAmnt*finalVec.x;
        this.y=(1-changeAmnt)*beginVec.y + changeAmnt*finalVec.y;
        this.z=(1-changeAmnt)*beginVec.z + changeAmnt*finalVec.z;
        return this;
    }

    /**
     * Check a vector... if it is null or its doubles are NaN or infinite,
     * return false.  Else return true.
     * @param vector the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValidVector(Vector3d vector) {
      if (vector == null) return false;
      if (Double.isNaN(vector.x) ||
          Double.isNaN(vector.y) ||
          Double.isNaN(vector.z)) return false;
      if (Double.isInfinite(vector.x) ||
          Double.isInfinite(vector.y) ||
          Double.isInfinite(vector.z)) return false;
      return true;
    }

    public static void generateOrthonormalBasis(Vector3d u, Vector3d v, Vector3d w) {
        w.normalizeLocal();
        generateComplementBasis(u, v, w);
    }

    public static void generateComplementBasis(Vector3d u, Vector3d v,
            Vector3d w) {
        double fInvLength;

        if (Math.abs(w.x) >= Math.abs(w.y)) {
            // w.x or w.z is the largest magnitude component, swap them
            fInvLength = 1.0 / Math.sqrt(w.x * w.x + w.z * w.z);
            u.x = -w.z * fInvLength;
            u.y = 0.0f;
            u.z = +w.x * fInvLength;
            v.x = w.y * u.z;
            v.y = w.z * u.x - w.x * u.z;
            v.z = -w.y * u.x;
        } else {
            // w.y or w.z is the largest magnitude component, swap them
            fInvLength = 1.0 / Math.sqrt(w.y * w.y + w.z * w.z);
            u.x = 0.0f;
            u.y = +w.z * fInvLength;
            u.z = -w.y * fInvLength;
            v.x = w.y * u.z - w.z * u.y;
            v.y = -w.x * u.z;
            v.z = w.x * u.y;
        }
    }

    @Override
    public Vector3d clone() {
        try {
            return (Vector3d) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can not happen
        }
    }

    /**
     * Saves this Vector3d into the given double[] object.
     * 
     * @param doubles
     *            The double[] to take this Vector3d. If null, a new double[3] is
     *            created.
     * @return The array, with X, Y, Z double values in that order
     */
    public double[] toArray(double[] doubles) {
//        if (doubles == null) {
//            doubles = new double[3];
//        }
        doubles[0] = x;
        doubles[1] = y;
        doubles[2] = z;
        return doubles;
    }

    /**
     * are these two vectors the same? they are is they both have the same x,y,
     * and z values.
     *
     * @param o
     *            the object to compare for equality
     * @return true if they are equal
     */
    public boolean equals(Object o) {
        if (!(o instanceof Vector3d)) { return false; }

        if (this == o) { return true; }

        Vector3d comp = (Vector3d) o;
        if (Double.compare(x,comp.x) != 0) return false;
        if (Double.compare(y,comp.y) != 0) return false;
        if (Double.compare(z,comp.z) != 0) return false;
        return true;
    }

    /**
     * <code>hashCode</code> returns a unique code for this vector object based
     * on it's values. If two vectors are logically equivalent, they will return
     * the same hash code value.
     * @return the hash code value of this vector.
     */
    public int hashCode() {
        int hash = 37;
        hash += 37 * hash + Float.floatToIntBits((float)x);
        hash += 37 * hash + Float.floatToIntBits((float)y);
        hash += 37 * hash + Float.floatToIntBits((float)z);
        return hash;
    }

    /**
     * <code>toString</code> returns the string representation of this vector.
     * The format is:
     *
     * org.jme.math.Vector3d [X=XX.XXXX, Y=YY.YYYY, Z=ZZ.ZZZZ]
     *
     * @return the string representation of this vector.
     */
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public void write(JmeExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(x, "x", 0);
        capsule.write(y, "y", 0);
        capsule.write(z, "z", 0);
    }

    public void read(JmeImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        x = capsule.readDouble("x", 0);
        y = capsule.readDouble("y", 0);
        z = capsule.readDouble("z", 0);
    }

    public double getX() {
        return x;
    }

    public Vector3d setX(double x) {
        this.x = x;
        return this;
    }

    public double getY() {
        return y;
    }

    public Vector3d setY(double y) {
        this.y = y;
        return this;
    }

    public double getZ() {
        return z;
    }

    public Vector3d setZ(double z) {
        this.z = z;
        return this;
    }
    
    /**
     * @param index
     * @return x value if index == 0, y value if index == 1 or z value if index ==
     *         2
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1, 2.
     */
    public double get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }
    
    /**
     * @param index
     *            which field index in this vector to set.
     * @param value
     *            to set to one of x, y or z.
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1, 2.
     */
    public void set(int index, double value) {
        switch (index) {
            case 0:
                x = value;
                return;
            case 1:
                y = value;
                return;
            case 2:
                z = value;
                return;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }
	public Vector3f toVector3f() {
		return new Vector3f((float)x,(float)y,(float)z);
	}
	public Vector3f toVector3f(Vector3f store) {
		store.x = (float)x;
		store.y = (float)y;
		store.z = (float)z;
		return store;
	}
	public Vector3d mult(Quaternion q) {
		float qx = q.getX();
        float qy = q.getY();
        float qz = q.getZ();
        float qw = q.getW();
        
        double vx = qy*z-qz*y;
        double vy = qz*x-qx*z;
        double vz = qx*y-qy*x;
        vx += vx; vy += vy; vz += vz;
        x += qw*vx + qy*vz-qz*vy;
        y += qw*vy + qz*vx-qx*vz;
        z += qw*vz + qx*vy-qy*vx;
		return this;
	}
	public Vector3d mult(Quaternion q, Vector3d store) {
		float qx = q.getX();
        float qy = q.getY();
        float qz = q.getZ();
        float qw = q.getW();
        
        double vx = qy*z-qz*y;
        double vy = qz*x-qx*z;
        double vz = qx*y-qy*x;
        vx += vx; vy += vy; vz += vz;
        store.x = x + qw*vx + qy*vz-qz*vy;
        store.y = y + qw*vy + qz*vx-qx*vz;
        store.z = z + qw*vz + qx*vy-qy*vx;
		return this;
	}
	public Vector3d transform(Transform t) {
		Vector3f v = t.getScale();
		multLocal(v.x, v.y, v.z);
		mult(t.getRotation());
		v = t.getTranslation();
		addLocal(v.x, v.y, v.z);
		return this;
	}
}
