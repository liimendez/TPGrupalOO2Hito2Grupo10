package com.grupo10.epicentrogourmet.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.grupo10.epicentrogourmet.entities.DetallePedido;
import com.grupo10.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.grupo10.epicentrogourmet.services.IDetallePedidoService;

@Service("detallePedidoService")
public class DetallePedidoService implements IDetallePedidoService {

    @Autowired
    private IDetallePedidoRepository detallePedidoRepository;

    @Override
    public List<DetallePedido> getAll() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public Optional<DetallePedido> findById(Long id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido insertOrUpdate(DetallePedido detalle) {
        return detallePedidoRepository.save(detalle);
    }

    @Override
    public boolean remove(Long id) {
        if (detallePedidoRepository.existsById(id)) {
            detallePedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}