import React, { useEffect, useRef, useState } from "react";
import { Button, ButtonGroup, Fieldset, Form, Grid, GridContainer, Icon, Label, Modal, ModalFooter, ModalHeading, ModalToggleButton, StepIndicator, StepIndicatorStep, TextInput } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import "../styles/buttonOutline.css";
import "../styles/StepIndicator.css";
import FormW2 from "../components/tax/forms/FormW2";
import Form1099 from "../components/tax/forms/Form1099";
import { useNavigate } from "react-router-dom";
import IncomeCard from "../components/tax/forms/IncomeCard";
import { useFindOneQuery } from "../app/api/taxApi";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { selectUserEmail } from "../app/features/userSlice";

export default function TaxIncomePage() {
    const { t } = useTranslation();

    const userEmail = useSelector(selectUserEmail);
    //const userEmail: string = "testingNewModel@gmail.com"; // temporary
    const { data, isLoading } = useFindOneQuery(userEmail);

    const modalRef = useRef(null);
    const [modalOpen, setModalOpen] = useState<boolean>(false);

    const handleModalClose = () => {
        setModalOpen(false);
    }

    const [activeForm, setActiveForm] = useState<string | null>(null);

    const handleW2ButtonClick = () => {
        setActiveForm("w2");
    };

    const handle1099ButtonClick = () => { 
        setActiveForm("1099");
    };

    const navigate = useNavigate();

    const handlePrev = () => {
        navigate('/tax-personal-info');
    }

    const handleNext = () => {
        navigate('/tax-deductions');
    }

    return (
        <>
            <GridContainer>
                <StepIndicator headingLevel="h4" className="bg-info-lighter" >
                    <StepIndicatorStep label={t("stepIndicator.personalInfo")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.income")} status="current" />
                    <StepIndicatorStep label={t("stepIndicator.deductionsCredits")} />
                    <StepIndicatorStep label={t("stepIndicator.taxOverview")} />
                </StepIndicator>
            </GridContainer>
            <br/>
            <div style={{display: "flex-wrap"}}>
            <GridContainer>
            <div className="grid-row flex-wrap">
                {data?.incomeSources.map((incomeSource, index) => (
                    <IncomeCard
                        key={index}
                        empID={incomeSource.empID}    
                        incomeType={incomeSource.incomeType}
                        state={incomeSource.state}
                        income={incomeSource.income}
                        fedWithheld={incomeSource.fedWithheld}
                        stateWithheld={incomeSource.stateWithheld}                
                    />
                ))}

                
                </div>
                
            </GridContainer>
            <GridContainer>
            <ModalToggleButton modalRef={modalRef} opener className="income-button add-income-button flex-align-self-center" style={{borderRadius: "10px"}}>
                    <div className="display-flex flex-column">
                    {t("taxIncomePage.addIncome")}
                    {/* <Icon.AddCircle className='usa-icon--size-7' style={{textAlign: "center"}}/> */}
                </div>

                    
            </ModalToggleButton>
            </GridContainer>
                <Modal
                    ref={modalRef}
                    id="example-modal-1"
                    aria-labelledby="modal-1-heading"
                    aria-describedby="modal-1-description">
                    <ModalHeading id="modal-1-heading" className="flex-align-center">
                        <div className="tax-income-page">
                            <Button type="button" className="bg-mint text-white" outline={activeForm === "w2"} onClick={handleW2ButtonClick} style={{borderRadius: "10px"}}>W2</Button>
                            <Button type="button" className="bg-mint text-white" outline={activeForm === "1099"} onClick={handle1099ButtonClick} style={{borderRadius: "10px"}}>1099</Button>
                        </div>
                    </ModalHeading>
                        {activeForm === "w2" && ((<FormW2 onClose={handleModalClose} />))}
                        {activeForm === "1099" && ((<Form1099 onClose={handleModalClose} />))}
                </Modal>
                </div>
            

            <GridContainer className="">
                <div className="mobile-lg:grid-col-4">
                    <Button className="margin-top-3 usa-button usa-button--outline text-mint" type="button" onClick={handlePrev} style={{borderRadius: "10px"}}>{"< " + t("stepIndicator.back")}</Button>
                    <Button className="bg-mint margin-top-3" type="button" onClick={handleNext} style={{borderRadius: "10px"}}>{t("stepIndicator.next") + " >"}</Button>
                </div>
            </GridContainer>
        </>
    );
}
