import DAO.DiscoDAO;
import DAO.HardwareDAO;
import DAO.ProcessoDAO;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.util.Conversor;
import entidades.Disco;
import entidades.Hardware;
import entidades.Processo;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@SuppressWarnings("SpellCheckingInspection")
public class Main {
    public static void main(String[] args) {
        SystemInfo systemInfo = new SystemInfo();
        Processador processador = new Processador();
        Memoria memoria = new Memoria();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


        HardwareExtraction hardwareExtraction = new HardwareExtraction();



        Runnable task = () -> {

            verticalLinesSout();
            textoFormatado("MÁQUINA");
            System.out.println("UUID "+systemInfo.getHardware().getComputerSystem().getHardwareUUID());
            System.out.println("Tamanho do uuid = " + systemInfo.getHardware().getComputerSystem().getHardwareUUID().length());

            verticalLinesSout();

            // Temperatura do núcleo do Processador
            verticalLinesSout();
            textoFormatado("PROCESSADOR");
            Double temperaturaProcessador = hardwareExtraction.getCpuTemperature();
            textoFormatado(String.format("Temperatura %.2f°C", temperaturaProcessador));


            // Uso do processador
            Double usoProcessador = processador.getUso();
            textoFormatado(String.format("Uso do processador: %.2f%%", usoProcessador));
            verticalLinesSout();

            //PROCESSADOR - FIM


            // Uso da memória atual, convertida para Double prontinha para enviar ao Banco
            textoFormatado("MEMORIA");
            String usoMemoria = Conversor.formatarBytes(memoria.getEmUso()).replaceAll("GiB","").replace(',','.');
            Double usoMemoriaDouble = Double.valueOf(usoMemoria);
            Double usoMemoriaPorcentagem = hardwareExtraction.getMemoryLoadPercentage();
            Double totalMemoria = usoMemoriaDouble * 100 / usoMemoriaPorcentagem;
            textoFormatado(String.format("Memória total: %.2fGB",totalMemoria));
            textoFormatado(String.format("Uso da memória: %.2fGB",usoMemoriaDouble));
            textoFormatado(String.format("Porcentagem de uso: %.2f%%",usoMemoriaPorcentagem));
            verticalLinesSout();
            //MEMORIA - FIM

            // DISCO

            List<String> paths = new ArrayList<>();
            HWDiskStore disco1 ;
            int qtdDiscos = systemInfo.getHardware().getDiskStores().size();
            for (int i = 0; i < qtdDiscos; i++) {
                disco1 = systemInfo.getHardware().getDiskStores().get(i);
                textoFormatado("DISCO");
                textoFormatado(String.format("Armazenamento %s",Conversor.formatarBytes(disco1.getSize()).replaceAll("GiB","GB")));
                paths.add(disco1.getPartitions().get(0).getMountPoint());
                File file = new File(paths.get(i));
                double size = file.getFreeSpace() / (1024.0 * 1024 * 1024);
                textoFormatado(String.format("Espaço livre %.2f GB", size));

                textoFormatado(String.format(disco1.getModel().replaceAll("(Unidades de disco padrão)", "")));
                verticalLinesSout();


            }

            // FIM - DISCO
            System.out.println();
            System.out.println();

            /*hardwareExtraction.getDiskInformation();
            Hardware hardware = new Hardware();
            hardware.setFkMaquina(400);
            hardware.setUsoProcessador(String.format("%.2f%%", usoProcessador));
            hardware.setTempProcessador(String.format("%.2f°C", temperaturaProcessador));
            hardware.setUsoMemoria(String.format("%.2fGB",usoMemoriaDouble));
            hardware.setArmazenamentoTotal(hardwareExtraction.discos.get(0).getArmazenamentoTotal());
            hardware.setArmazenamentoLivre(hardwareExtraction.discos.get(0).getArmazenamentoLivre());
            System.out.println("setei");
            new HardwareDAO().inserirDadosHardware(hardware);
            */

            List<Processo> processos = Processo.getProcessos();
            for (Processo processo: processos) {
                processo.setFkMaquina(400);
                new ProcessoDAO().inserirProcesso(processo);
            }

            List<Disco> discos = Disco.getDiscos();
            for (Disco disco: discos) {
                disco.setFkMaquina(400);
                new DiscoDAO().inserirDisco(disco);
            }
        };



       scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);


    }





    // Métodos apenas para formatação e embelezamento :)
    static void verticalLinesSout(){
        System.out.println("*-------------------------------*");
    }
    static void textoFormatado(String texto){
        System.out.printf("|  %s",texto);
        if (texto.length() > 26){
            for (int i = texto.length() + 3; i < 32; i++) {
                System.out.print(" ");
            }
        }
        for (int i = texto.length() + 3; i < 32; i++) {
            System.out.print(" ");
        }
        System.out.println("|");
    }
}


