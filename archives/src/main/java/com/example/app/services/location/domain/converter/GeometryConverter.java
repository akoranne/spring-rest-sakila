package com.example.app.services.location.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

@Converter
public class GeometryConverter implements AttributeConverter<Point, byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(Point point) {
        final WKBWriter wkbWriter = new WKBWriter();
        return wkbWriter.write(point);
    }

    @Override
    public Point convertToEntityAttribute(byte[] bytes) {
        final WKBReader wkbReader = new WKBReader(new GeometryFactory(new PrecisionModel(), 0));
        try {
            final Geometry geometry = wkbReader.read(bytes);
            if (geometry instanceof Point) {
                return (Point) geometry;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
