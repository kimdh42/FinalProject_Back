package synergyhubback.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synergyhubback.calendar.domain.entity.Label;
import synergyhubback.calendar.domain.repository.LabelRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;

    public Label createLabel(Label label) {
        return labelRepository.save(label);
    }

    public Optional<Label> findById(Long id) {
        return labelRepository.findById(id);
    }

    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }

    public Label updateLabel(Label label) {
        return labelRepository.save(label);
    }
}
