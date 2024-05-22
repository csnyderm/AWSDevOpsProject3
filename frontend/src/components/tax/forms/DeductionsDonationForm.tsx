import { Button, Fieldset, Form, Label, TextInput } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import "../../../styles/TaxDeductions.css";
import { Deduction, useFindOneQuery, useUpdateTaxReturnMutation } from "../../../app/api/taxApi";
import { useState } from "react";
import { List } from 'immutable';
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function DeductionsDonationForm() {

    const { t } = useTranslation();
    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
    const {data: taxReturnData, isLoading} = useFindOneQuery(userEmail);
    const [updateTaxReturn] = useUpdateTaxReturnMutation();
    
    //handle submitting of the form and updating filing status
    const [formData, setFormData] = useState({
        'cash': 0,
        'non-cash': 0,
    })

  //handle form changes
  const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const min = (amount: number) => {
    if (!isLoading && taxReturnData) {
      if (amount < taxReturnData.agi * 0.6) {
        return amount;
      } else {
        return taxReturnData.agi * 0.6;
      }
    }
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!isLoading && taxReturnData) {
      const charityAmount =
        Number(formData["cash"]) + Number(formData["non-cash"]);
      //add SLID (above-line deduction)
      if (charityAmount != 0) {
        const newDeduction: Deduction = {
          deductionType: "Charity",
          deductionAmount: min(charityAmount) || 0,
        };

        // Convert the existing array to an Immutable List
        const existingDeductionsImmutable = List(
          taxReturnData.belowLineDeductions
        );

        // Find the index of the deduction to update
        const existingDeductionIndex = existingDeductionsImmutable.findIndex(
          (deduction) => deduction.deductionType === "Charity"
        );

        if (existingDeductionIndex !== -1) {
          console.log("exists");

          // Update the existing deduction and create a new List
          const updatedDeductionsImmutable = existingDeductionsImmutable.set(
            existingDeductionIndex,
            newDeduction
          );

          // Convert the updated Immutable List back to a regular array
          const updatedDeductionsArray = updatedDeductionsImmutable.toArray();

          // Now you can use updatedDeductionsArray as needed
          const updatedTaxReturnData = {
            ...taxReturnData,
            belowLineDeductions: updatedDeductionsArray,
          };

          updateTaxReturn(updatedTaxReturnData);
        } else {
          //case where deduction doesn't exist yet
          // Add the new deduction to the deductions array
          const newAboveLineDeductions: Deduction[] = [
            ...taxReturnData.belowLineDeductions,
            newDeduction,
          ];

          const updatedTaxReturn = {
            ...taxReturnData,
            belowLineDeductions: newAboveLineDeductions,
          };
          updateTaxReturn(updatedTaxReturn);
        }
      }
    }
  };

  return (
    <>
      <Form onSubmit={handleSubmit}>
        <Fieldset legend="Donations">
          <h3>
          {t("taxDeductionsPage.enterDonations")}
          </h3>
          <div className="grid-row grid-gap">
            <div className="mobile-lg:grid-col-6">
              <Label htmlFor="ein">{t("taxDeductionsPage.checkOrCash")}</Label>
              <TextInput
                id="cash"
                name="cash"
                type="number"
                placeholder="0"
                onChange={handleFormChange}
              />
            </div>
          </div>
          <div className="grid-row grid-gap">
            <div className="mobile-lg:grid-col-6">
              <Label htmlFor="ein">{t("taxDeductionsPage.totalNonCash")}</Label>
              <TextInput
                id="non-cash"
                name="non-cash"
                type="number"
                placeholder="0"
                onChange={handleFormChange}
              />
            </div>
          </div>
        </Fieldset>
        <br />
        <Button type="submit" className="bg-mint" style={{borderRadius: "10px"}}>
        {t("taxDeductionsPage.submit")}
        </Button>
        <br />
      </Form>
    </>
  );
}
