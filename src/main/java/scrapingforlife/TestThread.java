package scrapingforlife;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.JOptionPane;


public class TestThread {
    
          
            public static Properties getProp() throws IOException {  //Gets external properties configs
            Properties props = new Properties();          
            FileInputStream file = new FileInputStream(//".\\config.properties");       
            "C:\\Users\\Rodrigo\\Desktop\\MascotAutoProject\\src\\main\\java\\scrapingforlife\\config.properties");
            props.load(file);
            return props;
 
            }
            
            public static void kill() throws IOException{
            int dialogButton = JOptionPane.OK_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would you like to stop?","Mascot Search's Running...",dialogButton);
                if(dialogResult == JOptionPane.OK_OPTION){
                    if(Boolean.valueOf(getProp().getProperty("close.opera.after.exit")) == true){
                        Runtime.getRuntime().exec("cmd /c taskkill /F /IM opera.exe");
                    }
                    Runtime.getRuntime().exec("cmd /c taskkill /F /IM operadriver.exe");
                    System.exit(0);                
                }    
            }
 

	public static void main(String args[]) throws IOException {
                         
                        
		final SearchModel searchModel = buildSearchModel();
		final List<Path> peakListPaths = buildPeakListPaths(searchModel.getPeakListFolderPath());
		
		final ExecutorService service = Executors.newFixedThreadPool(
                        Integer.parseInt(getProp().getProperty("threads.simultaneous")));// Number of simultaneous threads
		
		IntStream.range(0, peakListPaths.size())
			.forEach(i -> service.submit(new TestScrap(searchModel, peakListPaths.get(i))));
		service.shutdown();
                
                kill();
	}
       

	private static List<Path> buildPeakListPaths(final String peakListFolderPath) {
		final List<Path> peakListPaths = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(Paths.get(peakListFolderPath))) {
			peakListPaths.addAll(paths.filter(Files::isRegularFile).collect(Collectors.toList()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peakListPaths;
	}

	private static SearchModel buildSearchModel() throws IOException {
		final SearchModel searchModel = new SearchModel();
		searchModel.setName(getProp().getProperty("browse.name"));
		searchModel.setEmail(getProp().getProperty("browse.email"));
		searchModel.setDatabase(getProp().getProperty("browse.database"));
                searchModel.setTaxonomy(getProp().getProperty("browse.taxonomy"));
		searchModel.setPeakListFolderPath(getProp().getProperty("input.peaklists.path"));
		searchModel.setPeakListResultPath(getProp().getProperty("output.results.path"));
		return searchModel;
	}
}