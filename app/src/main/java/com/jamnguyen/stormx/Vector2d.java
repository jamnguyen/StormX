package com.jamnguyen.stormx;
public class Vector2d {

    /**
     * Constructs and initializes a Vector2d from the specified xyz coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
	protected double x;
	protected double y;
	protected double z;

    public Vector2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }


    /**
     * Constructs and initializes a Vector2d from the array of length 3.
     * @param v the array of length 3 containing xyz in order
     */
    public Vector2d(double[] v)
    {
		x = v[0];
		y = v[1];
    }


    /**
     * Constructs and initializes a Vector2d from the specified Vector2d.
     * @param v1 the Vector2d containing the initialization x y z data
     */
    public Vector2d(Vector2d v1)
    {
        this.x = v1.getX();
        this.y = v1.getY();
    }


  
    /**
     * Constructs and initializes a Vector2d to (0,0,0).
     */
    public Vector2d()
    {
       x = y = 0.0;
    }



    /**
     * Sets the value of this vector to the normalization of vector v1.
     * @param v1 the un-normalized vector
     */
    public void normalize(Vector2d v1)
    {
        double norm;

        norm = 1.0/Math.sqrt(v1.x*v1.x + v1.y*v1.y);
        this.x = v1.x*norm;
        this.y = v1.y*norm;
    }


    /**
     * Normalizes this vector in place.
     */
    public final void normalize()
    {
        double norm;

        norm = 1.0/Math.sqrt(this.x*this.x + this.y*this.y);
        this.x *= norm;
        this.y *= norm;
    }


  /**
   * Returns the dot product of this vector and vector v1.
   * @param v1 the other vector
   * @return the dot product of this and v1
   */
	public final double dot(Vector2d v1)
    {
		return (this.x*v1.x + this.y*v1.y);
    }


    /**
     * Returns the squared length of this vector.
     * @return the squared length of this vector
     */
    public double lengthSquared()
    {
        return (this.x*this.x + this.y*this.y);
    }


    /**
     * Returns the length of this vector.
     * @return the length of this vector
     */
    public final double length()
    {
        return Math.sqrt(this.x*this.x + this.y*this.y);
    }


  /**
    *   Returns the angle in radians between this vector and the vector
    *   parameter; the return value is constrained to the range [0,PI].
    *   @param v1    the other vector
    *   @return   the angle in radians in the range [0,PI]
    */
	public double angle(Vector2d v1)
	{
      double vDot = this.dot(v1) / ( this.length()*v1.length() );
      if( vDot < -1.0) vDot = -1.0;
      if( vDot >  1.0) vDot =  1.0;
      return((double) (Math.acos( vDot )));
	}
	public double angleACos(Vector2d v1)
	{
      double vDot = this.dot(v1) / ( this.length()*v1.length() );
      if( vDot < -1.0) vDot = -1.0;
      if( vDot >  1.0) vDot =  1.0;
      return vDot;
	}
	public void setX(double x)
	{
		this.x = x;
	}
	public double getX()
	{
		return x;
	}
	public void setY(double y)
	{
		this.y = y;
	}
	public double getY()
	{
		return y;
	}
	
	public void setVector2d(Vector2d v)
	{
		this.x = v.getX();
		this.y = v.getY();
	}
}