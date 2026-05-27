import React from 'react';
import { Menu, MenuItem, IconButton } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';

interface ActionsMenuProps {
  onEdit?: () => void;
  onDelete: () => void;
}

export const ActionsMenu: React.FC<ActionsMenuProps> = ({ onEdit, onDelete }) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <>
      <IconButton onClick={handleClick}>
        <MoreVertIcon />
      </IconButton>
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
        {onEdit && <MenuItem onClick={() => { onEdit(); handleClose(); }}>Изменить</MenuItem>}
        <MenuItem onClick={() => { onDelete(); handleClose(); }} sx={{ color: 'error.main' }}>Удалить</MenuItem>
      </Menu>
    </>
  );
};
