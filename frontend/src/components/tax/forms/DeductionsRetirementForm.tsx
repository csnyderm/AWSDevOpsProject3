import { Button, Fieldset, Form, Label, Radio, TextInput } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import "../../../styles/TaxDeductions.css";
import { useState } from "react";
import { Deduction, TaxReturn, useFindOneQuery, useUpdateTaxReturnMutation } from "../../../app/api/taxApi";
import { List } from 'immutable';
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function DeductionsRetirementForm() {

    const { t } = useTranslation();
    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
    const {data: taxReturnData, isLoading} = useFindOneQuery(userEmail);
    const [updateTaxReturn] = useUpdateTaxReturnMutation();

    //handle submitting of the form and updating filing status
    const [formData, setFormData] = useState({
        'ira': 0,
        '401k': 0,
    })

    //handle form changes
    const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    //TODO: this needs to be modifed to account for age
    const limitIra = (contribution: number) => {
        if (!isLoading && taxReturnData) {
            if (taxReturnData.filingStatus == 'MJ') {
                if (contribution < 15000) {
                    return contribution;
                } else {
                    return 15000;
                }
            } else {
                if (contribution < 7500) {
                    return contribution;
                } else {
                    return 7500;
                }
            }
        }
    }

    //TODO: needs to be modifed to account for age
    const limit401k = (contribution: number) => {
        if (!isLoading && taxReturnData) {
            if (taxReturnData.filingStatus == 'MJ') {
                if (contribution < 60000) {
                    return contribution;
                } else {
                    return 60000;
                }
            } else {
                if (contribution < 30000) {
                    return contribution;
                } else {
                    return 30000;
                }
            }
        }
    }

    const updateDeduction = (deductionType: string, newAmount: number, taxReturn: TaxReturn) => {
        const existingDeductionsImmutable = List(taxReturn.aboveLineDeductions);
        const existingDeductionIndex = existingDeductionsImmutable.findIndex(deduction => deduction.deductionType === deductionType);
      
        const newDeduction: Deduction = {
          deductionType,
          deductionAmount: newAmount || 0,
        };
      
        if (existingDeductionIndex !== -1) {
          // Update the existing deduction and create a new List
          const updatedDeductionsImmutable = existingDeductionsImmutable.set(existingDeductionIndex, newDeduction);
          
          // Convert the updated Immutable List back to a regular array
          const updatedDeductionsArray = updatedDeductionsImmutable.toArray();
          
          return {
            ...taxReturn,
            aboveLineDeductions: updatedDeductionsArray,
          };
        } else {
          // Add the new deduction to the deductions array
          const newAboveLineDeductions: Deduction[] = [
            ...taxReturn.aboveLineDeductions,
            newDeduction,
          ];
      
          return {
            ...taxReturn,
            aboveLineDeductions: newAboveLineDeductions,
          };
        }
    }
    
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!isLoading && taxReturnData) {
          let updatedTaxReturn = { ...taxReturnData };
      
          if (formData['ira'] !== 0) {
            updatedTaxReturn = updateDeduction("IRA", limitIra(formData['ira']) || 0, updatedTaxReturn);
          }
      
          if (formData['401k'] !== 0) {
            updatedTaxReturn = updateDeduction("401K", limit401k(formData['401k']) || 0, updatedTaxReturn);
          }
      
          updateTaxReturn(updatedTaxReturn);
        }
    }

    //TODO: add in a field checkbox for above age of 50
    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Fieldset legend="Education">
                    <h3>{t("taxDeductionsPage.retirementPlanDetails")}</h3>
                    
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.traditionalIRA")}</Label>
                            <TextInput 
                                id="ira" 
                                name="ira" 
                                type="number" 
                                placeholder="0"
                                onChange={handleFormChange}
                                 />
                            <Label htmlFor="ein">{t("taxDeductionsPage.401k")}</Label>
                            <TextInput 
                                id="401k" 
                                name="401k" 
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