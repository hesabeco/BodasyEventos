package com.app.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import com.app.web.entities.Categoria;
import com.app.web.entities.Traje;
import com.app.web.repository.CategoriaRepository;
import com.app.web.services.IUploadFileService;
import com.app.web.services.TrajeService;

@Controller
public class TrajeController {
    private final CategoriaRepository categoriaRepository;
    @Autowired
    private TrajeService trajeService;

    @Autowired
    private IUploadFileService uploadFileService;

    public TrajeController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/inicio/registroTraje")
    public String listarTrajes(Model model) {
        model.addAttribute("trajes", trajeService.obtenerTodosTrajes());
        return "listTraje";
    }

    @GetMapping("/inicio/registroTraje/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        List<Categoria> categorias = categoriaRepository.findAll();

        if (!categorias.isEmpty()) {
            model.addAttribute("categorias", categorias);
            model.addAttribute("traje", new Traje());
            return "RegisterTraje"; // Renderiza la vista si hay categorías
        } else {
            // Manejo de la situación en la que no hay categorías disponibles
            // Puedes redirigir a una página de error o realizar alguna otra acción.
            return "/inicio"; // Reemplaza "errorPage" con la vista de tu elección.
        }
    }

    @PostMapping("/inicio/registroTraje/nuevo")
    public String guardarTraje(@ModelAttribute Traje traje, @RequestParam("images") List<MultipartFile> images,
            SessionStatus status) throws IOException {
        List<String> imagenes = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                // Realiza el procesamiento de cada imagen aquí
                String uniqueFileName = uploadFileService.copy(image);
                // Puedes guardar estos nombres de archivo en una lista en tu entidad Meme, por
                // ejemplo
                imagenes.add(uniqueFileName);
            }
        }
        traje.setImagenes(imagenes);
        trajeService.guardarTraje(traje);
        status.setComplete();
        return "redirect:/inicio/registroTraje";

    }

    @GetMapping(value = "/inicio/registroTraje/uploads/{filename}")
    public ResponseEntity<Resource> goImage(@PathVariable String filename) {
        Resource resource = null;
        try {
            resource = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Traje traje = trajeService.obtenerTrajePorId(id);
        model.addAttribute("traje", traje);
        return "trajes/formulario";
    }

    /*
     * @PostMapping("/editar/{id}") public String actualizarTraje(@PathVariable
     * Integer id, @ModelAttribute Traje traje, @RequestParam("file") MultipartFile
     * file) { if (!file.isEmpty()) { try { byte[] imagenes = file.getBytes();
     * traje.setImagenes(imagenes); } catch (IOException e) { e.printStackTrace(); }
     * } trajeService.actualizarTraje(id, traje); return "redirect:/trajes"; }
     */

    @GetMapping("/inicio/registroTraje/{id}/borrar")
    public String borrarTraje(@PathVariable Integer id) {
        trajeService.borrarTraje(id);
        return "redirect:/inicio/registroTraje";
    }

    /*
     * @GetMapping("/inicio/registroTraje/ver-imagen/{idTraje}")
     * public String verImagen(@PathVariable Integer idTraje, Model model) {
     * Traje traje = trajeService.obtenerTrajePorId(idTraje);
     * if (traje != null && traje.getImagenes() != null) {
     * // Convierte el arreglo de bytes en una representación en Base64
     * String base64Image = Base64.getEncoder().encodeToString(traje.getImagenes());
     * 
     * // Agrega la imagen a la vista
     * model.addAttribute("imagenBase64", base64Image);
     * 
     * return "verImagen"; // Crea una nueva plantilla para mostrar la imagen
     * } else {
     * // Manejo de errores si el traje o la imagen no existen
     * return "error"; // Crea una vista de error
     * }
     * }
     */

}