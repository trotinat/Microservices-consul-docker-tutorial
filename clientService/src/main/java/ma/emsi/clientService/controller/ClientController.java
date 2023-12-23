package ma.emsi.clientService.controller;

import ma.emsi.clientService.entities.Client;
import ma.emsi.clientService.repositorires.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClientController {
    @Autowired
    ClientRepository clientRepository;
    @GetMapping("/clients")
    public List<Client> findAll(){
        return clientRepository.findAll();
    }
    @GetMapping("/clients/{id}")
    public Client findById(@PathVariable Long id) throws Exception{
        return this.clientRepository.findById(id).orElseThrow(()->new Exception("Client walo"));
    }
}
