package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private IUsuarioRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = repo.findOneByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("User not exists", username));
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        user.getRoles().forEach(rol -> roles.add(new SimpleGrantedAuthority(rol.getRol())));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Boolean.TRUE.equals(user.getDisponible()),
                true,
                true,
                true,
                roles
        );
    }
}
