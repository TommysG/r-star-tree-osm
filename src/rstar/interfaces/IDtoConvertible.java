package rstar.interfaces;

import rstar.dto.AbstractDTO;

public interface IDtoConvertible {
    <T extends AbstractDTO> T toDTO();
}