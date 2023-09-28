package org.springframework.samples.petclinic.developers;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Developer;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/developers")
public class DevelopersController {
    List<Developer> developers;

    @GetMapping
    public List<Developer> getDevelopers(){
        if(developers==null)
            loadDevelopers();
        return developers;        
    }

    private void loadDevelopers(){        
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new FileReader("pom.xml"));
            Person p=null;
            developers=model.getDevelopers();                                            
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }


}
