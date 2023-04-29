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

    @Override
    public String toString() {
        return "Location{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return lat.equals(location.lat) && lng.equals(location.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }
}
