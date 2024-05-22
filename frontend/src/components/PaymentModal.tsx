import React, { useState } from "react";
import { Button } from "@trussworks/react-uswds";

interface PaymentModalProps {
  amount: number;
  onAmountChange: (amount: number) => void;
  onSubmit: () => void;
}

const PaymentModal: React.FC<PaymentModalProps> = ({
  amount,
  onAmountChange,
  onSubmit,
}) => {
  const [isOpen, setIsOpen] = useState(false);

  const handleClose = () => {
    setIsOpen(false);
  };

  const handleOpen = () => {
    setIsOpen(true);
  };

  const handlePay = () => {
    onSubmit(); // Call the onSubmit function
    handleClose(); // Close the modal
  };

  const modalOverlayStyles: React.CSSProperties = {
    position: "fixed",
    top: 0,
    left: 0,
    width: "100%",
    height: "100%",
    background: "rgba(0, 0, 0, 0.5)",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1000,
  };

  const modalContentStyles = {
    background: "white",
    padding: "20px",
    borderRadius: "5px",
    boxShadow: "0px 2px 4px rgba(0, 0, 0, 0.2)",
  };

  const modalHeadingStyles = {
    margin: 0,
  };

  const modalButtonContainerStyles = {
    display: "flex",
    justifyContent: "flex-end",
    marginTop: "10px",
  };

  const modalButtonStyles = {
    padding: "10px 20px",
    background: "#mint",
    color: "white",
    border: "none",
    borderRadius: "3px",
    cursor: "pointer",
    marginRight: "10px",
  };

  const modalUnstyledButtonStyles = {
    background: "mint",
    color: "#0071BC",
  };

  return (
    <>
      <Button type="button" className="bg-mint open-modal-button "  onClick={handleOpen}>
        Pay
      </Button>
      {isOpen && (
        <div style={modalOverlayStyles}>
          <div style={modalContentStyles}>
            <h2 style={modalHeadingStyles}>Make Payment</h2>
            <label htmlFor="paymentAmount">Payment Amount:</label>
            <input
              type="number"
              id="paymentAmount"
              value={amount}
              onChange={(e) => onAmountChange(parseFloat(e.target.value))}
              step="0.01"
              min="0"
            />
            <div style={modalButtonContainerStyles}>
              <button style={modalButtonStyles} onClick={handlePay}>
                Pay
              </button>
              <button
                style={{ ...modalButtonStyles, ...modalUnstyledButtonStyles }}
                onClick={handleClose}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default PaymentModal;
