package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.repositories.RelGrpOptArtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelGrpOptArtService {

    @Autowired
    private RelGrpOptArtRepository relGrpOptArtRepository;

    public List<?> getGroupesOptions(Integer bouticid, Integer artId) {
        return relGrpOptArtRepository.findByCustomidAndArtId(bouticid, artId);
    }
}
