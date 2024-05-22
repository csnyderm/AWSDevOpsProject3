import React, { useState } from 'react';

interface EditModalProps {
  onClose: () => void;
  onUpdate: (updatedData: any) => void;
  fields: Array<{ name: string; placeholder: string }>;
  initialValues: any;
  collectionType: string;
}

const EditModal: React.FC<EditModalProps> = ({ onClose, onUpdate, fields, initialValues }) => {
  const [editedData, setEditedData] = useState<any>(initialValues || {});

  const handleFieldChange = (e: React.ChangeEvent<HTMLInputElement>, fieldName: string) => {
    setEditedData((prevData: any) => ({
      ...prevData,
      [fieldName]: e.target.value,
    }));
  };

  const handleSubmit = () => {
    onUpdate(editedData);
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

  const modalButtonStyles: React.CSSProperties = {
    backgroundColor: '#04c585',
    color: '#fff',
    padding: '10px 20px',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginRight: '10px',
  };

  return (
    <div style={modalOverlayStyles}>
      <div style={modalContentStyles}>
        <h2>Edit Entry</h2>
        <form>
          {fields.map((field) => (
            <div key={field.name}>
              <label htmlFor={field.name}>{field.placeholder}</label>
              <br /> {/* Add a line break */}
              <input
                type="text"
                name={field.name}
                placeholder={field.placeholder}
                value={editedData[field.name] || ''}
                onChange={(e) => handleFieldChange(e, field.name)}
              />
            </div>
          ))}
          <div className="modal-buttons">
            <button style={modalButtonStyles} type="button" onClick={handleSubmit}>
              Save
            </button>
            <button style={modalButtonStyles} type="button" onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditModal;
