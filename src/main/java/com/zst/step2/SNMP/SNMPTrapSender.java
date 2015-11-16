package com.zst.step2.SNMP;

import org.snmp4j.*;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;

public class SNMPTrapSender {

    public static final String community = "private";

    //  Sending Trap for sysLocation of RFC1213
    public static final String trapOid = ".1.3.6.1.2.1.1.6";

    public static final String ipAddress = "127.0.0.1";

    public static final int port = 162;

    private SNMPTrapSender() {
    }

    private static PDU createPdu(int snmpVersion, String msg) {
        PDU pdu = DefaultPDUFactory.createPDU(snmpVersion);
        if (snmpVersion == SnmpConstants.version1) {
            pdu.setType(PDU.V1TRAP);
        } else {
            pdu.setType(PDU.TRAP);
        }
        pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
        pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
                new IpAddress(ipAddress)));
        pdu.add(new VariableBinding(new OID(trapOid), new OctetString(msg)));
        return pdu;
    }

    public static void sendSnmpV1V2Trap(int version, String msg) {
        sendV1orV2Trap(version, community, ipAddress, port, msg);
    }

    private static void sendV1orV2Trap(int snmpVersion, String community,
                                       String ipAddress, int port, String msg) {
        try {
            // create v1/v2 PDU
            PDU snmpPDU = createPdu(snmpVersion, msg);

            // Create Transport Mapping
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(community));
            comtarget.setVersion(snmpVersion);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            snmp.send(snmpPDU, comtarget);
            System.out.println("Sent Trap to (IP:Port)=> " + ipAddress + ":"
                    + port);
            snmp.close();
        } catch (Exception e) {
            System.err.println("Error in Sending Trap to (IP:Port)=> "
                    + ipAddress + ":" + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }

    public static void sendSnmpV3Trap(String msg) {
        try {
            Address targetAddress = GenericAddress.parse("udp:" + ipAddress
                    + "/" + port);
            TransportMapping<?> transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            USM usm = new USM(SecurityProtocols.getInstance()
                    .addDefaultProtocols(), new OctetString(
                    MPv3.createLocalEngineID()), 0);
            SecurityProtocols.getInstance()
                    .addPrivacyProtocol(new PrivAES192());
            SecurityModels.getInstance().addSecurityModel(usm);
            transport.listen();

            snmp.getUSM().addUser(
                    new OctetString("MD5DES"),
                    new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                            new OctetString("UserName"), PrivAES128.ID,
                            new OctetString("UserName")));

            // Create Target
            UserTarget target = new UserTarget();
            target.setAddress(targetAddress);
            target.setRetries(1);
            target.setTimeout(11500);
            target.setVersion(SnmpConstants.version3);
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString("MD5DES"));

            // Create PDU for V3
            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(ScopedPDU.NOTIFICATION);
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID,
                    SnmpConstants.linkDown));
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString(
                    msg)));

            // Send the PDU
            snmp.send(pdu, target);
            System.out.println("Sending Trap to (IP:Port)=> " + ipAddress + ":"
                    + port);
            snmp.addCommandResponder(new CommandResponder() {
                public void processPdu(CommandResponderEvent arg0) {
                    System.out.println(arg0);
                }
            });
            snmp.close();
        } catch (Exception e) {
            System.err.println("Error in Sending Trap to (IP:Port)=> "
                    + ipAddress + ":" + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }
}
