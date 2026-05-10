package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upc.qhurinet.dtos.ArchivoUploadResponseDTO;
import pe.edu.upc.qhurinet.servicesinterfaces.IArchivoStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ArchivoStorageServiceImplement implements IArchivoStorageService {
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "pdf");

    @Value("${qhurinet.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public ArchivoUploadResponseDTO guardarImagen(MultipartFile file, String carpeta) throws IOException {
        return guardar(file, carpeta, IMAGE_EXTENSIONS);
    }

    @Override
    public ArchivoUploadResponseDTO guardarDocumento(MultipartFile file, String carpeta) throws IOException {
        return guardar(file, carpeta, DOCUMENT_EXTENSIONS);
    }

    private ArchivoUploadResponseDTO guardar(MultipartFile file, String carpeta, Set<String> extensionesPermitidas) throws IOException {
        validar(file);
        String nombreOriginal = StringUtils.cleanPath(file.getOriginalFilename() == null ? "archivo" : file.getOriginalFilename());
        String extension = extension(nombreOriginal);
        if (!extensionesPermitidas.contains(extension)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido");
        }

        String carpetaSegura = carpeta == null || carpeta.isBlank() ? "general" : carpeta.replaceAll("[^a-zA-Z0-9_-]", "-");
        String nombreArchivo = UUID.randomUUID() + "." + extension;
        Path destinoDir = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(carpetaSegura).normalize();
        Files.createDirectories(destinoDir);
        Path destino = destinoDir.resolve(nombreArchivo).normalize();
        if (!destino.startsWith(destinoDir)) {
            throw new IllegalArgumentException("Nombre de archivo invalido");
        }

        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        ArchivoUploadResponseDTO response = new ArchivoUploadResponseDTO();
        response.setUrl("/uploads/" + carpetaSegura + "/" + nombreArchivo);
        response.setNombreArchivo(nombreArchivo);
        response.setNombreOriginal(nombreOriginal);
        response.setContentType(file.getContentType());
        response.setSize(file.getSize());
        return response;
    }

    private void validar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo obligatorio");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("El archivo excede el limite de 5 MB");
        }
    }

    private String extension(String nombreOriginal) {
        int dot = nombreOriginal.lastIndexOf('.');
        if (dot < 0 || dot == nombreOriginal.length() - 1) {
            throw new IllegalArgumentException("El archivo debe tener extension");
        }
        return nombreOriginal.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
