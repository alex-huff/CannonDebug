package dev.phonis.networking;

import java.io.DataOutputStream;
import java.io.IOException;

public interface CDSerializable {

    void toBytes(DataOutputStream dos) throws IOException;

}
