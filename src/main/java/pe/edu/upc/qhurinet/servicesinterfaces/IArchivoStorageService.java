package pe.edu.upc.qhurinet.servicesinterfaces;

import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.qhurinet.dtos.ArchivoUploadResponseDTO;

import java.io.IOException;

public interface IArchivoStorageService {
    ArchivoUploadResponseDTO guardarImagen(MultipartFile file, String carpeta) throws IOException;
    ArchivoUploadResponseDTO guardarDocumento(MultipartFile file, String carpeta) throws IOException;
}
