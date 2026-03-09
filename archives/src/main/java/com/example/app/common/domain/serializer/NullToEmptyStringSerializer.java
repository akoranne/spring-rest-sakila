package com.example.app.common.domain.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.Type;

/**
 * Custom null serializer that represents null values as empty strings in JSON.
 */
public class NullToEmptyStringSerializer extends StdSerializer<Object> {
    @Serial
    private static final long serialVersionUID = 1L;

    public NullToEmptyStringSerializer() {
        this(null);
    }

    public NullToEmptyStringSerializer(Class<Object> t) {
        super(t);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString("");
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers,
                                  TypeSerializer typeSer)
            throws IOException {
        gen.writeString("");
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("empty_string");
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitor.expectNullFormat(typeHint);
    }
}
