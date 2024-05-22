import { Button, Fieldset, Form, Label, TextInput } from "@trussworks/react-uswds";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useFindOneQuery, useUpdateTaxReturnMutation } from "../../../app/api/taxApi";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../../../app/features/userSlice";

export default function DeductionsfamilyForm() {

    const { t } = useTranslation();

    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
    const {data: taxReturnData, isLoading} = useFindOneQuery(userEmail);
    const [updateTaxReturn] = useUpdateTaxReturnMutation();

    //handle submitting of the form and updating filing status
    const [formData, setFormData] = useState({
        'child' : 0,
        'other' : 0,
    })
    
    //handle form changes
    const handleFormChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = event.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!isLoading && taxReturnData) {
            const updatedTaxReturn = {
                ...taxReturnData,
                childDependents: formData['child'],
                otherDependents: formData['other'],
            }
            updateTaxReturn(updatedTaxReturn);
        } 
    }

    return (
        <>
            <Form onSubmit={handleSubmit}>
                <Fieldset legend="Dependents">
                    <h3 className="margin-bottom-0">{t("taxDeductionsPage.tellUsTheNumber")}</h3>
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.childDependents")}</Label>
                            <TextInput 
                                id="child" 
                                name="child" 
                                type="number" 
                                placeholder="0"
                                onChange={handleFormChange}
                                 />
                        </div>
                        {/* <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">Full-time students age 17-23</Label>
                            <TextInput 
                                id="ein" 
                                name="ein" 
                                type="text" 
                                placeholder="0" 
                                 />
                        </div> */}
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">{t("taxDeductionsPage.otherDependents")}</Label>
                            <TextInput 
                                id="other" 
                                name="other" 
                                type="number" 
                                placeholder="0" 
                                onChange={handleFormChange}
                                 />
                        </div>
                    </div>
                   
                </Fieldset>
              
                {/* <Fieldset legend="Childcare">
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">Number of children under 13 who received child care</Label>
                            <TextInput 
                                id="ein" 
                                name="ein" 
                                type="text" 
                                placeholder="0" 
                                 />
                        </div>
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein" style={{marginTop: "2.7em"}}>Total child care expenses</Label>
                            <TextInput 
                                id="ein" 
                                name="ein" 
                                type="text" 
                                placeholder="0" 
                                
                                 />
                        </div>
                    </div>
                    <div className="grid-row grid-gap">
                        <div className="mobile-lg:grid-col-6">
                            <Label htmlFor="ein">How much in childcare reimbursement was received in 2023?</Label>
                            <TextInput 
                                id="ein" 
                                name="ein" 
                                type="text" 
                                placeholder="0" 
                                 />
                        </div>
                    </div>
                </Fieldset> */}
          
                <Button type="submit" className="bg-mint" style={{borderRadius: "10px"}}>{t("taxDeductionsPage.submit")}</Button>
                <br/>
            </Form>
        </>
    )
}