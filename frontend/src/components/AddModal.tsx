import React, { useState } from 'react';

interface NewItem {
  [key: string]: string;
}

interface AddModalProps {
  onClose: () => void;
  onSave: (newItem: NewItem) => void;
  fields: { name: string; placeholder: string }[];
}

const AddModal: React.FC<AddModalProps> = ({ onClose, onSave, fields }) => {
  const [newItem, setNewItem] = useState<NewItem>({});

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;

    // Update the state with the new value for the corresponding field name
    setNewItem((prevItem) => ({ ...prevItem, [name]: value }));
  };

  const handleSave = () => {
    onSave(newItem);
    onClose();
  };

  const modalOverlayStyles: React.CSSProperties = {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000, // Adjust as needed
  };

  const modalContentStyles: React.CSSProperties = {
    backgroundColor: '#fff',
    padding: '20px',
    borderRadius: '8px',
    boxShadow: '0px 2px 10px rgba(0, 0, 0, 0.2)',
  };

  const modalHeadingStyles: React.CSSProperties = {
    fontSize: '24px',
    marginBottom: '10px',
  };

  const inputContainerStyles: React.CSSProperties = {
    marginBottom: '10px',
    display: 'flex',
    flexDirection: 'column',
  };

  const inputStyles: React.CSSProperties = {
    padding: '5px',
  };

  const buttonContainerStyles: React.CSSProperties = {
    display: 'flex',
    justifyContent: 'flex-end',
    marginTop: '10px',
  };

  const modalButtonStyles: React.CSSProperties = {
    backgroundColor: '#04c585',
    color: '#fff',
    padding: '10px 20px',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginRight: '10px',
  };

  const modalUnstyledButtonStyles: React.CSSProperties = {
    backgroundColor: '#04c585',
    color: '#fff',
  };

  return (
    <div style={modalOverlayStyles}>
      <div style={modalContentStyles}>
        <h2 style={modalHeadingStyles}>Add New Item</h2>
        {fields.map((field, index) => (
          <div key={index} style={inputContainerStyles}>
            <label>{field.placeholder}</label>
            <input
              type="text"
              name={field.name}
              placeholder={field.placeholder}
              value={newItem[field.name] || ''}
              onChange={handleInputChange}
              style={inputStyles}
            />
          </div>
        ))}
        <div style={buttonContainerStyles}>
          <button style={modalButtonStyles} onClick={handleSave}>
            Save
          </button>
          <button
            style={{ ...modalButtonStyles, ...modalUnstyledButtonStyles }}
            onClick={onClose}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export default AddModal;
