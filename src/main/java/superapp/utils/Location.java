package superapp.utils;

import java.util.Objects;

/**
 * The Location class represents a geographic location with latitude and longitude coordinates.
 */
public class Location {

    private Double lat; // The latitude of the location
    private Double lng; // The longitude of the location.

    /**
     * Constructs a new empty Location object.
     */
    public Location() {
    }

    /**
     * Constructs a new Location object with the given latitude and longitude coordinates.
     *
     * @param lat The latitude of the location.
     * @param lng The longitude of the location.
     */
    public Location(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Gets the latitude of the location.
     *
     * @return The latitude of the location.
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Gets the longitude of the location.
     *
     * @return The longitude of the location.
     */
    public Double getLng() {
        return lng;
    }

    /**
     * Sets the latitude of the location. Throws a RuntimeException if the latitude is not valid.
     *
     * @param lat The latitude of the location.
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Sets the longitude of the location. Throws a RuntimeException if the longitude is not valid.
     *
     * @param lng The longitude of the location.
     */
    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     * Returns a string representation of the Location object.
     * <p>
     * The string representation is in the format "Location{lat=<lat>, lng=<lng>}".
     * The <lat> and <lng> placeholders are replaced with the actual latitude and longitude values.
     *
     * @return a string representation of the Location object
     */
    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * Two Location objects are considered equal if their latitude and longitude values are equal.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the o argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return lat.equals(location.lat) && lng.equals(location.lng);
    }

    /**
     * Returns a hash code value for the Location object.
     * <p>
     * The hash code is generated based on the latitude and longitude values.
     *
     * @return a hash code value for the Location object
     */
    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
}
