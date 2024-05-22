import { Accordion, Button, GridContainer, StepIndicator, StepIndicatorStep } from "@trussworks/react-uswds";
import { AccordionItemProps } from "@trussworks/react-uswds/lib/components/Accordion/Accordion";
import { useTranslation } from "react-i18next";
import "../styles/TaxDeductions.css";
import "../styles/StepIndicator.css";
import DeductionsfamilyForm from "../components/tax/forms/DeductionsFamilyForm";
import { useNavigate } from "react-router-dom";
import DeductionsEducationForm from "../components/tax/forms/DeductionsEducationForm";
import DeductionsDonationForm from "../components/tax/forms/DeductionsDonationForm";
import DeductionsRetirementForm from "../components/tax/forms/DeductionsRetirementForm";
import DeductionMedicalForm from "../components/tax/forms/DeductionsMedicalForm";

export default function TaxDeductionsCreditsPage() {

    const { t } = useTranslation();

    const navigate = useNavigate();

    const handlePrev = () => {
        navigate('/tax-income');
    }

    const handleNext = () => {
        navigate('/tax-overview');
    }

    const items: AccordionItemProps[] = [
        {
            title: (
                <div style={{display: "flex"}}>
                    <img src="./src/assets/deductions_family.png" alt="Family" className="deductions" />
                    <h3>{t("taxDeductionsPage.family")}</h3>
                </div>
            ),
            content: <DeductionsfamilyForm />,
            expanded: false,
            id: 'accordian-family',
            headingLevel: 'h1',
        },
        {
            title: (
                <div style={{display: "flex"}}>
                    <img src="./src/assets/deductions_education.png" alt="Education" className="deductions" />
                    <h3>{t("taxDeductionsPage.education")}</h3>
                </div>
            ),
            content: <DeductionsEducationForm />,
            expanded: false,
            id: 'accordian-education',
            headingLevel: 'h1',
        },
        {
            title: (
                <div style={{display: "flex"}}>
                    <img src="./src/assets/deductions_retirement.png" alt="Retirement" className="deductions" />
                    <h3>{t("taxDeductionsPage.retirement")}</h3>
                </div>
            ),
            content: <DeductionsRetirementForm />,
            expanded: false,
            id: 'accordian-retirement',
            headingLevel: 'h1',
        },
        {
            title: (
                <div style={{display: "flex"}}>
                    <img src="./src/assets/deductions_donations.png" alt="Donations" className="deductions" />
                    <h3>{t("taxDeductionsPage.donations")}</h3>
                </div>
            ),
            content: <DeductionsDonationForm />,
            expanded: false,
            id: 'accordian-donations',
            headingLevel: 'h1',
        },
        {
            title: (
                <div style={{display: "flex"}}>
                    <img src="./src/assets/deductions_medical.png" alt="Medical" className="deductions" />
                    <h3>{t("taxDeductionsPage.medical")}</h3>
                </div>
            ),
            content: <DeductionMedicalForm />,
            expanded: false,
            id: 'accordian-medical',
            headingLevel: 'h1',
        }
    ];
    
    return (
        <>
            <GridContainer>
                <StepIndicator headingLevel="h4" className="custom-step-indicator" >
                    <StepIndicatorStep label={t("stepIndicator.personalInfo")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.income")} status="complete" />
                    <StepIndicatorStep label={t("stepIndicator.deductionsCredits")} status="current"/>
                    <StepIndicatorStep label={t("stepIndicator.taxOverview")} />
                </StepIndicator>
                <br/>
                <Accordion bordered={true} items={items} className="custom-accordian"/>
            </GridContainer>
            <br/>
            <br/>
            
            <GridContainer className="">
                <div className="mobile-lg:grid-col-4">
                    <Button className="margin-top-3 usa-button usa-button--outline" type="button" onClick={handlePrev} style={{borderRadius: "10px"}}>{"< " + t("stepIndicator.back")}</Button>
                    <Button className="bg-mint margin-top-3" type="button" onClick={handleNext} style={{borderRadius: "10px"}}>{t("stepIndicator.next") + " >"}</Button>
                </div>
            </GridContainer>
            
            
        </>
    )
}