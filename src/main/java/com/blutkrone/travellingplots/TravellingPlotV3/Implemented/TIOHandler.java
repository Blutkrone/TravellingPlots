package com.blutkrone.travellingplots.TravellingPlotV3.Implemented;

import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IBuildInstruction;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IOHandler;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.IPlotSignature;
import com.blutkrone.travellingplots.TravellingPlotV3.Abstract.ITravellingPlot;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Collection.BuildInstructionQueue;
import com.blutkrone.travellingplots.TravellingPlotV3.Implemented.Serializable.TPlotSignature;
import com.blutkrone.travellingplots.TravellingPlotV3.TravellingPlotHandler;
import com.blutkrone.travellingplots.TravellingPlots;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Optional;

public class TIOHandler implements IOHandler<FSTConfiguration> {

    private static final String sep = File.separator;

    private final TravellingPlots plugin;
    private final TravellingPlotHandler travellingPlotHandler;

    private final FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();

    public TIOHandler(TravellingPlots travellingPlots, TravellingPlotHandler travellingPlotHandler) {
        this.travellingPlotHandler = travellingPlotHandler;
        plugin = travellingPlots;
    }

    public File validateFile(File f) {
        try {
            if (!f.getParentFile().exists())
                f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public File getRecoveryFile(Chunk chunk) {
        return new File(plugin.getDataFolder().getAbsolutePath() + sep + "recovery" + sep + chunk.getWorld().getName(), String.format("X%sZ%s.tp", chunk.getX(), chunk.getZ()));
    }

    public File getSignatureFile(OfflinePlayer offlinePlayer) {
        return new File(plugin.getDataFolder().getAbsolutePath() + sep + "plots" + sep + offlinePlayer.getUniqueId(), "signature.ts");
    }

    public File getPlotFile(OfflinePlayer offlinePlayer, int x, int z) {
        return new File(plugin.getDataFolder().getAbsolutePath() + sep + "plots" + sep + offlinePlayer.getUniqueId(), String.format("X%sZ%s.tp", x, z));
    }

    @Override
    public FSTConfiguration getSerializer() {
        return fst;
    }

    @Override
    public BuildInstructionQueue readInstructions(File file) throws IOException {
        if (!file.exists()) return new BuildInstructionQueue();
        FileInputStream stream;
        FSTObjectInput input = fst.getObjectInput(stream = new FileInputStream(validateFile(file)));
        BuildInstructionQueue tBuildInstructions = null;
        try {
            tBuildInstructions = ((BuildInstructionQueue) input.readObject(BuildInstructionQueue.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        stream.close();
        return tBuildInstructions;
    }

    @Override
    public Optional<IPlotSignature> readSignature(OfflinePlayer offlinePlayer) throws IOException {
        if (!getSignatureFile(offlinePlayer).exists()) return Optional.empty();
        try {
            FileInputStream stream;
            FSTObjectInput input = fst.getObjectInput(stream = new FileInputStream(validateFile(getSignatureFile(offlinePlayer))));
            TPlotSignature plotSignature = (TPlotSignature) input.readObject(TPlotSignature.class);
            stream.close();
            return Optional.ofNullable(plotSignature);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public BuildInstructionQueue readPlotInstructions(OfflinePlayer owner, int x, int z) throws IOException {
        return readInstructions(getPlotFile(owner, x, z));
    }

    @Override
    public File getRestoringFile(Chunk chunkToRestore) throws IOException {
        return getRecoveryFile(chunkToRestore);
    }

    @Override
    public void saveSignature(OfflinePlayer offlinePlayer, ITravellingPlot travellingPlot) throws IOException {
        FileOutputStream stream;
        FSTObjectOutput out = fst.getObjectOutput(stream = new FileOutputStream(validateFile(getSignatureFile(offlinePlayer))));
        out.writeObject(travellingPlotHandler.makeSignature(offlinePlayer, travellingPlot));
        out.flush();
        stream.close();
    }

    @Override
    public void saveInstructions(AbstractQueue<? extends IBuildInstruction> instructions, File output) throws IOException {
        if (!(instructions instanceof BuildInstructionQueue))
            throw new IOException("Cannot write " + instructions.getClass() + " to the output!");
        FileOutputStream stream;
        FSTObjectOutput out = fst.getObjectOutput(stream = new FileOutputStream(validateFile(output)));
        out.writeObject(instructions);
        out.flush();
        stream.close();
    }

    @Override
    public void saveInstructions(OfflinePlayer offlinePlayer, int x, int z, AbstractQueue<? extends IBuildInstruction> instructions) throws IOException {
        if (!(instructions instanceof BuildInstructionQueue))
            throw new IOException("Cannot write " + instructions.getClass() + " to the output!");
        FileOutputStream stream;
        FSTObjectOutput out = fst.getObjectOutput(stream = new FileOutputStream(validateFile(getPlotFile(offlinePlayer, x, z))));
        out.writeObject(instructions);
        out.flush();
        stream.close();
    }
}
