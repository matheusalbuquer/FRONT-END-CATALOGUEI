package group.triade.catalogo.controller;

import group.triade.catalogo.dtos.AdminRequestDTO;
import group.triade.catalogo.dtos.AdminResponseDTO;
import group.triade.catalogo.entities.Admin;
import group.triade.catalogo.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponseDTO> criar (@RequestBody  AdminRequestDTO dto){
        AdminResponseDTO salvo = adminService.criar(dto);
        return ResponseEntity.ok(salvo);
    }

}
