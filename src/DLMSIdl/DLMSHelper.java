package DLMSIdl;


/**
* DLMSIdl/DLMSHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DLMS.idl
* Thursday, 7 March, 2019 1:21:05 AM IST
*/

abstract public class DLMSHelper
{
  private static String  _id = "IDL:DLMSIdl/DLMS:1.0";

  public static void insert (org.omg.CORBA.Any a, DLMSIdl.DLMS that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DLMSIdl.DLMS extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DLMSIdl.DLMSHelper.id (), "DLMS");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DLMSIdl.DLMS read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DLMSStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DLMSIdl.DLMS value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DLMSIdl.DLMS narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DLMSIdl.DLMS)
      return (DLMSIdl.DLMS)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DLMSIdl._DLMSStub stub = new DLMSIdl._DLMSStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DLMSIdl.DLMS unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DLMSIdl.DLMS)
      return (DLMSIdl.DLMS)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      DLMSIdl._DLMSStub stub = new DLMSIdl._DLMSStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
