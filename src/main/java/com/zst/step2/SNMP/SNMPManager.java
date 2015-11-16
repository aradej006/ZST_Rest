package com.zst.step2.SNMP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

public class SNMPManager {

    Snmp snmp = null;
    String address = null;

    public SNMPManager(String add) {
        address = add;
    }

    public void start() throws IOException {
        TransportMapping transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        // Do not forget this line!
        transport.listen();
    }

    public String getAsString(OID oid) throws IOException {
        ResponseEvent event = get(oid);
        return event.getResponse().get(0).getVariable().toString();
    }

    public String getNextAsString(OID oid) throws IOException {
        ResponseEvent event = getNext(oid);
        return event.getResponse().get(0).getVariable().toString();
    }

    private ResponseEvent get(OID oid) throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GET);
        ResponseEvent event = snmp.get(pdu, getTarget());
        if (event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    private ResponseEvent getNext(OID oid) throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid));
        pdu.setType(PDU.GETNEXT);
        ResponseEvent event = snmp.getNext(pdu, getTarget());
        if (event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    public ResponseEvent set(OID oid, String value) throws IOException {
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(oid, new OctetString(value)));
        pdu.setType(PDU.SET);
        ResponseEvent event = snmp.set(pdu, getTarget());
        if (event != null) {
            return event;
        }
        throw new RuntimeException("GET timed out");
    }

    public ResponseEvent setSysContact(String sysContact) throws IOException {
        return set(CONST.sysContact, sysContact);
    }

    public String getSysContact() throws IOException {
        return getAsString(CONST.sysContact);
    }

    public List<List<String>> getTable(OID oid) {
        OID[] oids = new OID[]{oid};
        TableUtils tableUtils = new TableUtils(snmp, new DefaultPDUFactory());
        List<TableEvent> events = tableUtils.getTable(getTarget(), oids, null, null);
        List<List<String>> list = new ArrayList<List<String>>();

        if (events.get(0).isError()) {
            throw new RuntimeException(events.get(0).getErrorMessage());
        }
        int columns = events.get(events.size() - 1).getIndex().get(1);
        int rows = events.size() / columns;
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<String>();
            for (int j = 0; j < columns; j++) {
                row.add(events.get(j * rows + i).getColumns()[0].getVariable().toString());
            }
            list.add(row);
        }
        return list;
    }

    private Target getTarget() {
        Address targetAddress = GenericAddress.parse(address);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("private"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

}
