import com.github.britooo.looca.api.util.Conversor;
import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.sensors.Load;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import entidades.Disco;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HardwareExtraction {
    SystemInfo systemInfo = new SystemInfo();
    Double cpuTemperature;
    Double memoryLoadPercentage;

    List<Disco> discos = new ArrayList<>();


    //Método para pegar todas as informações de disco necessárias e armazená-las em uma lista de discos
    public void  getDiskInformation(){
        List<String> paths = new ArrayList<>();
        int qtdDiscos =  systemInfo.getHardware().getDiskStores().size();
        HWDiskStore HWDisk;
        for (int i = 0; i < qtdDiscos; i++) {
            Disco disco = new Disco();
            HWDisk = systemInfo.getHardware().getDiskStores().get(i);
            paths.add(HWDisk.getPartitions().get(0).getMountPoint());
            File file = new File(paths.get(i));
            String diskModel = HWDisk.getModel().replaceAll("[(Unidades de disco padrão)]", "");
            String totalSpace = (Conversor.formatarBytes(HWDisk.getSize()).replaceAll("TiB","TB").replaceAll("MiB","MB").replaceAll("GiB","GB").replace(',','.'));
            String freeSpace = Conversor.formatarBytes(file.getFreeSpace()).replaceAll("TiB","TB").replaceAll("MiB","MB").replaceAll("GiB","GB").replace(',','.');


            disco.setArmazenamentoLivre(freeSpace);
            disco.setModelo(diskModel);
            disco.setArmazenamentoTotal(totalSpace);

            discos.add(disco);
        }


    }



    public Double getCpuTemperature(){
        Components components = JSensors.get.components();
        List<com.profesorfalken.jsensors.model.components.Cpu> cpus = components.cpus;
        List<Temperature> temperatures = cpus.get(0).sensors.temperatures;
        List<Load> loads = cpus.get(0).sensors.loads;

          if (cpus != null) {
            for (final com.profesorfalken.jsensors.model.components.Cpu cpu : cpus) {
                //System.out.println("Found CPU component: " + cpu.name);
                if (cpu.sensors.temperatures.isEmpty()) {
                    System.out.println("Nenhum sensor de temperatura foi encontrado");
                    return 0.0;
                }
            }
        }
        Double packageTemperature = temperatures.get(temperatures.size()-1).value;

        return packageTemperature;
    }



    public Double getMemoryLoadPercentage(){
        Components components = JSensors.get.components();
        List<Load> loads = components.cpus.get(0).sensors.loads;

        for (Load load:loads) {
            if (load.name.contains("Memory")){
                return load.value;
            }
        }
        return 0.0;
    }




}
