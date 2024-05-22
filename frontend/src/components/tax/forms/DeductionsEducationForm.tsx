import { Button, Fieldset, Form, Label, Radio, TextInput } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import "../../../styles/TaxDeductions.css";
import { Credit, Deduction, useFindOneQuery, useUpdateTaxReturnMutation } from "../../../app/api/taxApi";
import { useState } from "react";
import { List } from 'immutable';
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function DeductionsEducationForm() {

    const { t } = useTranslation();
    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
    const {data: taxReturnData, isLoading} = useFindOneQuery(userEmail);
    const [updateTaxReturn] = useUpdateTaxReturnMutation();

    //handle submitting of the form and updating filing status
    const [formData, setFormData] = useState({
        'attend-college-first': "false",
        'tuition': 0,
        'loan' : 0,
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
        if (amount < 2500) {
            return amount;
        } else {
            return 2500;
        }
    }
    
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
     
        if (!isLoading && taxReturnData) {
            console.log("tax data found")
            //add AOTC (credit)
            if (formData["attend-college-first"] === "true") {
                const updatedTaxReturn = {
                    ...taxReturnData,
                    aotcClaims: 1
                };
                updateTaxReturn(updatedTaxReturn);
            } else {
                const updatedTaxReturn = {
                    ...taxReturnData,
                    aotcClaims: 0
                };
                updateTaxReturn(updatedTaxReturn);
            }
    
            //add SLID (above-line deduction)
            if (formData["loan"] != 0) {
                const newDeduction: Deduction = {
                    deductionType: "SLID",
                    deductionAmount: min(formData.loan)
                }
                
                // Convert the existing array to an Immutable List
                const existingDeductionsImmutable = List(taxReturnData.aboveLineDeductions);

                // Find the index of the deduction to update
                const existingDeductionIndex = existingDeductionsImmutable.findIndex(
                deduction => deduction.deductionType === "SLID"
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
                        aboveLineDeductions: updatedDeductionsArray
                    };
                    
                    updateTaxReturn(updatedTaxReturnData);
                } else { //case where deduction doesn't exist yet
                    // Add the new deduction to the deductions array
                    const newAboveLineDeductions: Deduction[] = [
                        ...taxReturnData.aboveLineDeductions,
                        newDeduction,
                    ];

                    const updatedTaxReturn = {
                        ...taxReturnData,
                        aboveLineDeductions: newAboveLineDeductions
                    }
                    console.log(updateTaxReturn)
                    updateTaxReturn(updatedTaxReturn);
                }
            } 
        }
    }

    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Fieldset legend="Education">
                    <h3>{t("taxDeductionsPage.attendCollegeOrStudentLoan")}</h3>
                    <div>
                    <h3>{t("taxDeductionsPage.firstFourYears")}</h3>
                    <Radio
                        id="attend-college-first"
                        name="attend-college-first"
                        label={t("Yes")}
                        value="true"
                        onChange={handleFormChange}
                    />
                    <Radio
                        id="attend-college-first-no"
                        name="attend-college-first"
                        label={t("No")}
                        value="false"
                        onChange={handleFormChange}
                    />
                    </div>
                </Fieldset>
                <br/><br/>
                <Fieldset legend="Expenses">
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.tuitionFeesPaid")}</Label>
                            <TextInput 
                                id="tuition" 
                                name="tuition" 
                                type="number" 
                                placeholder="0" 
                                onChange={handleFormChange}
                            />
                        </div>
                    </div>
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.studentLoanInterest")}</Label>
                            <TextInput 
                                id="loan" 
                                name="loan" 
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
