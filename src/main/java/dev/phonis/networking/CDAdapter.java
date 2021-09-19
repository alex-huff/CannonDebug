package dev.phonis.networking;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.EntityTracker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

public class CDAdapter {

    public static List<CDPacket> payloadFromBlockSelections(List<BlockSelection> selections, boolean byOrder) throws IOException {
        return CDAdapter.payloadFromCDHistory(CDAdapter.fromBlockSelections(selections, byOrder));
    }

    public static List<CDPacket> payloadFromCDHistory(CDHistory history) throws IOException {
        List<CDPacket> packets = new LinkedList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        history.toBytes(dos);
        dos.close();

        byte[] data = baos.toByteArray();
        int current = 0;
        int left = data.length;
        CDStartHistory startHistory = new CDStartHistory(left);

        packets.add(startHistory);

        while (left > 0) {
            int currentChunk = Math.min(left, CDHistorySegment.maxLength);
            byte[] segmentData = new byte[currentChunk];

            System.arraycopy(data, current, segmentData, 0, currentChunk);

            left -= currentChunk;
            current += currentChunk;

            packets.add(new CDHistorySegment(segmentData));
        }

        return packets;
    }

    public static CDHistory fromBlockSelections(List<BlockSelection> selections, boolean byOrder) {
        OptionalLong optionalMinTick = selections.parallelStream().filter(
            blockSelection -> blockSelection.getTracker() != null
        ).mapToLong(
            blockSelection -> blockSelection.getTracker().getSpawnTick()
        ).min();

        if (optionalMinTick.isPresent()) {
            long minTick = optionalMinTick.getAsLong();

            return new CDHistory(
                selections.parallelStream().filter(
                    blockSelection -> blockSelection.getTracker() != null
                ).map(
                    blockSelection -> CDAdapter.fromBlockSelection(blockSelection, minTick)
                ).collect(Collectors.toList()),
                byOrder
            );
        }

        return new CDHistory(Collections.emptyList(), byOrder);
    }

    public static CDBlockSelection fromBlockSelection(BlockSelection selection, long minTick) {
        return new CDBlockSelection(
            selection.getId(),
            CDAdapter.fromLocation(selection.getLocation()),
            selection.getOrder(),
            CDAdapter.fromEntityTracker(selection.getTracker(), minTick)
        );
    }

    public static CDLocation fromLocation(Location location) {
        return new CDLocation(location.getX(), location.getY(), location.getZ());
    }

    public static CDVec3D fromVector(Vector vector) {
        return new CDVec3D(vector.getX(), vector.getY(), vector.getZ());
    }

    public static CDEntityTracker fromEntityTracker(EntityTracker tracker, long minTick) {
        return new CDEntityTracker(
            CDAdapter.fromEntityType(tracker.getEntityType()),
            tracker.getSpawnTick() - minTick,
            tracker.getLocationHistory().parallelStream().map(CDAdapter::fromLocation).collect(Collectors.toList()),
            tracker.getVelocityHistory().parallelStream().map(CDAdapter::fromVector).collect(Collectors.toList()),
            tracker.getDeathTick() - minTick
        );
    }

    public static CDEntityType fromEntityType(EntityType entityType) {
        if (entityType.equals(EntityType.FALLING_BLOCK)) {
            return CDEntityType.FALLINGBLOCK;
        } else if (entityType.equals(EntityType.PRIMED_TNT)) {
            return CDEntityType.TNT;
        } else {
            return CDEntityType.OTHER;
        }
    }

}
