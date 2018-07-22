/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.github.ddth.recipes.apiservice.thrift.def;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2018-07-13")
public class TApiParams implements org.apache.thrift.TBase<TApiParams, TApiParams._Fields>, java.io.Serializable, Cloneable, Comparable<TApiParams> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TApiParams");

  private static final org.apache.thrift.protocol.TField ENCODING_FIELD_DESC = new org.apache.thrift.protocol.TField("encoding", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField PARAMS_DATA_FIELD_DESC = new org.apache.thrift.protocol.TField("paramsData", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField EXPECTED_RETURN_ENCODING_FIELD_DESC = new org.apache.thrift.protocol.TField("expectedReturnEncoding", org.apache.thrift.protocol.TType.I32, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TApiParamsStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TApiParamsTupleSchemeFactory();

  /**
   * 
   * @see TDataEncoding
   */
  public TDataEncoding encoding; // optional
  public java.nio.ByteBuffer paramsData; // optional
  /**
   * 
   * @see TDataEncoding
   */
  public TDataEncoding expectedReturnEncoding; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see TDataEncoding
     */
    ENCODING((short)1, "encoding"),
    PARAMS_DATA((short)2, "paramsData"),
    /**
     * 
     * @see TDataEncoding
     */
    EXPECTED_RETURN_ENCODING((short)3, "expectedReturnEncoding");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // ENCODING
          return ENCODING;
        case 2: // PARAMS_DATA
          return PARAMS_DATA;
        case 3: // EXPECTED_RETURN_ENCODING
          return EXPECTED_RETURN_ENCODING;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final _Fields optionals[] = {_Fields.ENCODING,_Fields.PARAMS_DATA,_Fields.EXPECTED_RETURN_ENCODING};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.ENCODING, new org.apache.thrift.meta_data.FieldMetaData("encoding", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TDataEncoding.class)));
    tmpMap.put(_Fields.PARAMS_DATA, new org.apache.thrift.meta_data.FieldMetaData("paramsData", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING        , true)));
    tmpMap.put(_Fields.EXPECTED_RETURN_ENCODING, new org.apache.thrift.meta_data.FieldMetaData("expectedReturnEncoding", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TDataEncoding.class)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TApiParams.class, metaDataMap);
  }

  public TApiParams() {
    this.encoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.JSON_STRING;

    this.expectedReturnEncoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.JSON_DEFAULT;

  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TApiParams(TApiParams other) {
    if (other.isSetEncoding()) {
      this.encoding = other.encoding;
    }
    if (other.isSetParamsData()) {
      this.paramsData = org.apache.thrift.TBaseHelper.copyBinary(other.paramsData);
    }
    if (other.isSetExpectedReturnEncoding()) {
      this.expectedReturnEncoding = other.expectedReturnEncoding;
    }
  }

  public TApiParams deepCopy() {
    return new TApiParams(this);
  }

  @Override
  public void clear() {
    this.encoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.JSON_STRING;

    this.paramsData = null;
    this.expectedReturnEncoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.JSON_DEFAULT;

  }

  /**
   * 
   * @see TDataEncoding
   */
  public TDataEncoding getEncoding() {
    return this.encoding;
  }

  /**
   * 
   * @see TDataEncoding
   */
  public TApiParams setEncoding(TDataEncoding encoding) {
    this.encoding = encoding;
    return this;
  }

  public void unsetEncoding() {
    this.encoding = null;
  }

  /** Returns true if field encoding is set (has been assigned a value) and false otherwise */
  public boolean isSetEncoding() {
    return this.encoding != null;
  }

  public void setEncodingIsSet(boolean value) {
    if (!value) {
      this.encoding = null;
    }
  }

  public byte[] getParamsData() {
    setParamsData(org.apache.thrift.TBaseHelper.rightSize(paramsData));
    return paramsData == null ? null : paramsData.array();
  }

  public java.nio.ByteBuffer bufferForParamsData() {
    return org.apache.thrift.TBaseHelper.copyBinary(paramsData);
  }

  public TApiParams setParamsData(byte[] paramsData) {
    this.paramsData = paramsData == null ? (java.nio.ByteBuffer)null : java.nio.ByteBuffer.wrap(paramsData.clone());
    return this;
  }

  public TApiParams setParamsData(java.nio.ByteBuffer paramsData) {
    this.paramsData = org.apache.thrift.TBaseHelper.copyBinary(paramsData);
    return this;
  }

  public void unsetParamsData() {
    this.paramsData = null;
  }

  /** Returns true if field paramsData is set (has been assigned a value) and false otherwise */
  public boolean isSetParamsData() {
    return this.paramsData != null;
  }

  public void setParamsDataIsSet(boolean value) {
    if (!value) {
      this.paramsData = null;
    }
  }

  /**
   * 
   * @see TDataEncoding
   */
  public TDataEncoding getExpectedReturnEncoding() {
    return this.expectedReturnEncoding;
  }

  /**
   * 
   * @see TDataEncoding
   */
  public TApiParams setExpectedReturnEncoding(TDataEncoding expectedReturnEncoding) {
    this.expectedReturnEncoding = expectedReturnEncoding;
    return this;
  }

  public void unsetExpectedReturnEncoding() {
    this.expectedReturnEncoding = null;
  }

  /** Returns true if field expectedReturnEncoding is set (has been assigned a value) and false otherwise */
  public boolean isSetExpectedReturnEncoding() {
    return this.expectedReturnEncoding != null;
  }

  public void setExpectedReturnEncodingIsSet(boolean value) {
    if (!value) {
      this.expectedReturnEncoding = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case ENCODING:
      if (value == null) {
        unsetEncoding();
      } else {
        setEncoding((TDataEncoding)value);
      }
      break;

    case PARAMS_DATA:
      if (value == null) {
        unsetParamsData();
      } else {
        if (value instanceof byte[]) {
          setParamsData((byte[])value);
        } else {
          setParamsData((java.nio.ByteBuffer)value);
        }
      }
      break;

    case EXPECTED_RETURN_ENCODING:
      if (value == null) {
        unsetExpectedReturnEncoding();
      } else {
        setExpectedReturnEncoding((TDataEncoding)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case ENCODING:
      return getEncoding();

    case PARAMS_DATA:
      return getParamsData();

    case EXPECTED_RETURN_ENCODING:
      return getExpectedReturnEncoding();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case ENCODING:
      return isSetEncoding();
    case PARAMS_DATA:
      return isSetParamsData();
    case EXPECTED_RETURN_ENCODING:
      return isSetExpectedReturnEncoding();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TApiParams)
      return this.equals((TApiParams)that);
    return false;
  }

  public boolean equals(TApiParams that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_encoding = true && this.isSetEncoding();
    boolean that_present_encoding = true && that.isSetEncoding();
    if (this_present_encoding || that_present_encoding) {
      if (!(this_present_encoding && that_present_encoding))
        return false;
      if (!this.encoding.equals(that.encoding))
        return false;
    }

    boolean this_present_paramsData = true && this.isSetParamsData();
    boolean that_present_paramsData = true && that.isSetParamsData();
    if (this_present_paramsData || that_present_paramsData) {
      if (!(this_present_paramsData && that_present_paramsData))
        return false;
      if (!this.paramsData.equals(that.paramsData))
        return false;
    }

    boolean this_present_expectedReturnEncoding = true && this.isSetExpectedReturnEncoding();
    boolean that_present_expectedReturnEncoding = true && that.isSetExpectedReturnEncoding();
    if (this_present_expectedReturnEncoding || that_present_expectedReturnEncoding) {
      if (!(this_present_expectedReturnEncoding && that_present_expectedReturnEncoding))
        return false;
      if (!this.expectedReturnEncoding.equals(that.expectedReturnEncoding))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetEncoding()) ? 131071 : 524287);
    if (isSetEncoding())
      hashCode = hashCode * 8191 + encoding.getValue();

    hashCode = hashCode * 8191 + ((isSetParamsData()) ? 131071 : 524287);
    if (isSetParamsData())
      hashCode = hashCode * 8191 + paramsData.hashCode();

    hashCode = hashCode * 8191 + ((isSetExpectedReturnEncoding()) ? 131071 : 524287);
    if (isSetExpectedReturnEncoding())
      hashCode = hashCode * 8191 + expectedReturnEncoding.getValue();

    return hashCode;
  }

  @Override
  public int compareTo(TApiParams other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetEncoding()).compareTo(other.isSetEncoding());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEncoding()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.encoding, other.encoding);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetParamsData()).compareTo(other.isSetParamsData());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetParamsData()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.paramsData, other.paramsData);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetExpectedReturnEncoding()).compareTo(other.isSetExpectedReturnEncoding());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetExpectedReturnEncoding()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.expectedReturnEncoding, other.expectedReturnEncoding);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TApiParams(");
    boolean first = true;

    if (isSetEncoding()) {
      sb.append("encoding:");
      if (this.encoding == null) {
        sb.append("null");
      } else {
        sb.append(this.encoding);
      }
      first = false;
    }
    if (isSetParamsData()) {
      if (!first) sb.append(", ");
      sb.append("paramsData:");
      if (this.paramsData == null) {
        sb.append("null");
      } else {
        org.apache.thrift.TBaseHelper.toString(this.paramsData, sb);
      }
      first = false;
    }
    if (isSetExpectedReturnEncoding()) {
      if (!first) sb.append(", ");
      sb.append("expectedReturnEncoding:");
      if (this.expectedReturnEncoding == null) {
        sb.append("null");
      } else {
        sb.append(this.expectedReturnEncoding);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TApiParamsStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TApiParamsStandardScheme getScheme() {
      return new TApiParamsStandardScheme();
    }
  }

  private static class TApiParamsStandardScheme extends org.apache.thrift.scheme.StandardScheme<TApiParams> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TApiParams struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // ENCODING
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.encoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.findByValue(iprot.readI32());
              struct.setEncodingIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PARAMS_DATA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.paramsData = iprot.readBinary();
              struct.setParamsDataIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // EXPECTED_RETURN_ENCODING
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.expectedReturnEncoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.findByValue(iprot.readI32());
              struct.setExpectedReturnEncodingIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TApiParams struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.encoding != null) {
        if (struct.isSetEncoding()) {
          oprot.writeFieldBegin(ENCODING_FIELD_DESC);
          oprot.writeI32(struct.encoding.getValue());
          oprot.writeFieldEnd();
        }
      }
      if (struct.paramsData != null) {
        if (struct.isSetParamsData()) {
          oprot.writeFieldBegin(PARAMS_DATA_FIELD_DESC);
          oprot.writeBinary(struct.paramsData);
          oprot.writeFieldEnd();
        }
      }
      if (struct.expectedReturnEncoding != null) {
        if (struct.isSetExpectedReturnEncoding()) {
          oprot.writeFieldBegin(EXPECTED_RETURN_ENCODING_FIELD_DESC);
          oprot.writeI32(struct.expectedReturnEncoding.getValue());
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TApiParamsTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TApiParamsTupleScheme getScheme() {
      return new TApiParamsTupleScheme();
    }
  }

  private static class TApiParamsTupleScheme extends org.apache.thrift.scheme.TupleScheme<TApiParams> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TApiParams struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetEncoding()) {
        optionals.set(0);
      }
      if (struct.isSetParamsData()) {
        optionals.set(1);
      }
      if (struct.isSetExpectedReturnEncoding()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetEncoding()) {
        oprot.writeI32(struct.encoding.getValue());
      }
      if (struct.isSetParamsData()) {
        oprot.writeBinary(struct.paramsData);
      }
      if (struct.isSetExpectedReturnEncoding()) {
        oprot.writeI32(struct.expectedReturnEncoding.getValue());
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TApiParams struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.encoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.findByValue(iprot.readI32());
        struct.setEncodingIsSet(true);
      }
      if (incoming.get(1)) {
        struct.paramsData = iprot.readBinary();
        struct.setParamsDataIsSet(true);
      }
      if (incoming.get(2)) {
        struct.expectedReturnEncoding = com.github.ddth.recipes.apiservice.thrift.def.TDataEncoding.findByValue(iprot.readI32());
        struct.setExpectedReturnEncodingIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
