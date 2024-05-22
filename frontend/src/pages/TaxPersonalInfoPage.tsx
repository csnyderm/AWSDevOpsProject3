import { Button, GridContainer, StepIndicator, StepIndicatorStep } from "@trussworks/react-uswds";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import FilingStatusForm from "../components/tax/forms/FilingStatusForm";

const TaxPersonalInfoPage = (): React.ReactNode => {
    const { t } = useTranslation();

    const navigate = useNavigate();

    const handleNext = () => {
        navigate('/tax-income');
    }
    return (
        <>
            <GridContainer>
                <StepIndicator headingLevel="h4" className="bg-info-lighter" >
                    <StepIndicatorStep label={t("stepIndicator.personalInfo")} status="current" />
                    <StepIndicatorStep label={t("stepIndicator.income")}/>
                    <StepIndicatorStep label={t("stepIndicator.deductionsCredits")} />
                    <StepIndicatorStep label={t("stepIndicator.taxOverview")} />
                </StepIndicator>
            </GridContainer>

            <GridContainer className="">
                <FilingStatusForm></FilingStatusForm>
                {/* <div className="mobile-lg:grid-col-4">
                    <Button className="bg-mint margin-top-3" type="button" onClick={handleNext} style={{borderRadius: "10px"}}>{t("stepIndicator.next") + " >"}</Button>
                </div> */}
            </GridContainer>
            
        </>
    );
};

export default TaxPersonalInfoPage;