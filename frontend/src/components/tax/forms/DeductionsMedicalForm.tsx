import { Button, Fieldset, Form, Label, TextInput } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import "../../../styles/TaxDeductions.css";
import { Deduction, useFindOneQuery, useUpdateTaxReturnMutation } from "../../../app/api/taxApi";
import { useState } from "react";
import { List } from 'immutable';
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function DeductionMedicalForm() {

    const { t } = useTranslation();

    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
    const {data: taxReturnData, isLoading} = useFindOneQuery(userEmail);
    const [updateTaxReturn] = useUpdateTaxReturnMutation();

    //handle submitting of the form and updating filing status
    const [formData, setFormData] = useState({
        'medical': 0,
    })

    //handle form changes
    const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const limitMedical = (amount: number) => {
        if(!isLoading && taxReturnData) {
            const calculatedAmount = amount - (taxReturnData.agi * 0.075);

            if (calculatedAmount < 0) {
                return 0;
            }

            if (taxReturnData.filingStatus == 'MJ') {
                if (calculatedAmount < 15000) {
                    return calculatedAmount;
                } else {
                    return 15000;
                }
            } else {
                if (calculatedAmount < 7500) {
                    return calculatedAmount;
                } else {
                    return 7500;
                }
            }
        }
    }
    
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!isLoading && taxReturnData) {
            //add SLID (above-line deduction)
            if ( formData['medical'] != 0) {
                const newDeduction: Deduction = {
                    deductionType: "Medical",
                    deductionAmount: limitMedical(formData['medical']) || 0,
                }
                
                // Convert the existing array to an Immutable List
                const existingDeductionsImmutable = List(taxReturnData.belowLineDeductions)

                // Find the index of the deduction to update
                const existingDeductionIndex = existingDeductionsImmutable.findIndex(
                deduction => deduction.deductionType === "Medical"
                );

                if (existingDeductionIndex !== -1) {
                    console.log("exists");
                    
                    // Update the existing deduction and create a new List
                    const updatedDeductionsImmutable = existingDeductionsImmutable.set(existingDeductionIndex, newDeduction);
                    
                    // Convert the updated Immutable List back to a regular array
                    const updatedDeductionsArray = updatedDeductionsImmutable.toArray();
                    
                    // Now you can use updatedDeductionsArray as needed
                    const updatedTaxReturnData = {
                        ...taxReturnData,
                        belowLineDeductions: updatedDeductionsArray
                    };
                    
                    updateTaxReturn(updatedTaxReturnData);

                } else { //case where deduction doesn't exist yet
                    // Add the new deduction to the deductions array
                    const newBelowLineDeductions: Deduction[] = [
                        ...taxReturnData.belowLineDeductions,
                        newDeduction,
                    ];

                    const updatedTaxReturn = {
                        ...taxReturnData,
                        belowLineDeductions: newBelowLineDeductions
                    }
                    updateTaxReturn(updatedTaxReturn);
                }
            }
        }
    } 

    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Fieldset legend="Medical">
                    <h3>{t("taxDeductionsPage.medicalAmount")}
                    </h3>
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.medicalExpenses")}</Label>
                            <TextInput 
                                id="medical" 
                                name="medical" 
                                type="number" 
                                placeholder="0"
                                onChange={handleFormChange}
                                 />
                        </div>
                    </div>
                </Fieldset>
                <br/>
                <Button type="submit" className="bg-mint" style={{borderRadius: "10px"}}>{t("taxDeductionsPage.submit")}</Button>
                <br/>
            </Form>
        </>
    )
}