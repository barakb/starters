package com.totango.rsocketavro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.io.AvroDecodeFormat
import com.github.avrokotlin.avro4k.io.AvroEncodeFormat
import com.github.avrokotlin.avro4k.serializer.LocalDateSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.DecoderFactory
import java.io.ByteArrayOutputStream
import java.time.LocalDate

@Serializable
data class Message(
    val payload: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val purchaseDate: LocalDate = LocalDate.now()
)

@Serializable
data class Message1(
    val payload: String? = null,
    @Serializable(with = LocalDateSerializer::class)
    val purchaseDate: LocalDate = LocalDate.now(),
    val desc: String = ""
)


fun <T> writeData(t: T, serializer: SerializationStrategy<T>): ByteArray {
    val schema = Avro.default.schema(serializer)
    val out = ByteArrayOutputStream()
    val avro = Avro.default.openOutputStream(serializer) {
        encodeFormat = AvroEncodeFormat.Data()
        this.schema = schema
    }.to(out)
    avro.write(t)
    avro.close()
    return out.toByteArray()
}

fun <T> readJson(bytes: ByteArray, serializer: KSerializer<T>): GenericRecord {
    val schema = Avro.default.schema(serializer)
    val datumReader = GenericDatumReader<GenericRecord>(schema)
    val decoder = DecoderFactory.get().jsonDecoder(schema, SeekableByteArrayInput(bytes))
    return datumReader.read(null, decoder)
}

fun <T> writeJson(t: T, serializer: KSerializer<T>): ByteArray {
    val schema = Avro.default.schema(serializer)
    val baos = ByteArrayOutputStream()
    val output = Avro.default.openOutputStream(serializer) {
        encodeFormat = AvroEncodeFormat.Json
        this.schema = schema
    }.to(baos)
    output.write(t)
    output.close()
    return baos.toByteArray()
}

fun <T> readData(bytes: ByteArray, serializer: KSerializer<T>): GenericRecord {
    val schema = Avro.default.schema(serializer)
    val avro = Avro.default.openInputStream {
        decodeFormat = AvroDecodeFormat.Data(schema)
    }.from(bytes)
    return avro.next() as GenericRecord
}

fun <T> writeBinary(t: T, serializer: SerializationStrategy<T>): ByteArray {
    val schema = Avro.default.schema(serializer)
    val out = ByteArrayOutputStream()
    val avro = Avro.default.openOutputStream(serializer) {
        encodeFormat = AvroEncodeFormat.Binary
        this.schema = schema
    }.to(out)
    avro.write(t)
    avro.close()
    return out.toByteArray()
}

fun <T> readBinary(bytes: ByteArray, serializer: KSerializer<T>): GenericRecord {
    val schema = Avro.default.schema(serializer)
    val datumReader = GenericDatumReader<GenericRecord>(schema)
    val decoder = DecoderFactory.get().binaryDecoder(SeekableByteArrayInput(bytes), null)
    return datumReader.read(null, decoder)
}
