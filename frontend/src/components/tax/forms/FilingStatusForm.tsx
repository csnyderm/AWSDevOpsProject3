import {
  Button,
  Fieldset,
  Form,
  GridContainer,
  Radio,
} from "@trussworks/react-uswds";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import {
  useFindOneQuery,
  useNewReturnMutation,
  useUpdateTaxReturnMutation,
} from "../../../app/api/taxApi";
import { useSelector } from "react-redux";
import { selectUserEmail } from "../../../app/features/userSlice";
import { useNavigate } from "react-router-dom";

export default function FilingStatusForm() {
  const { t } = useTranslation();

  const navigate = useNavigate();
  const userEmail = useSelector(selectUserEmail);
  //const userEmail: string = "testingNewModel@gmail.com"; //for now until login functionality is working
  const { data: taxReturnData, isLoading } = useFindOneQuery(userEmail);
  const [updateTaxReturn] = useUpdateTaxReturnMutation();
  const [createTaxReturn] = useNewReturnMutation();

  //handle submitting of the form and updating filing status
  const [formData, setFormData] = useState({
    "married-single": "",
    "filing-status": "",
  });

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
        filingStatus: formData["filing-status"],
      };
      updateTaxReturn(updatedTaxReturn);
    } else {
      const newTaxReturn = {
        email: userEmail,
        filingStatus: formData["filing-status"],
        totalIncome: 0,
        totalStateOwed: 0,
        totalFedOwed: 0,
        taxableIncome: 0,
        agi: 0,
        childDependents: 0,
        otherDependents: 0,
        aotcClaims: 0,
        educationalExpenditures: [],
        incomeSources: [],
        belowLineDeductions: [],
        aboveLineDeductions: [],
        credits: [],
      };
      createTaxReturn(newTaxReturn);
    }
    navigate("/tax-income");
  };

  return (
    <>
      <GridContainer>
        <div
          style={{
            backgroundColor: "white",
            boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
            borderRadius: "8px",
            padding: "2em",
          }}
        >
          <Form onSubmit={handleSubmit}>
            <Fieldset
              legend={t("taxPersonalInfo.filingStatus")}
              legendStyle="large"
              style={{ minWidth: "25vw" }}
            >
              <br />
              <div>
                <h3>{t("Will you file as single or married?")}</h3>
                <Radio
                  id="single"
                  name="married-single"
                  label={t("taxPersonalInfo.single")}
                  value="single"
                  checked={formData["married-single"] === "single"}
                  onChange={handleFormChange}
                  required
                />
                <Radio
                  id="married"
                  name="married-single"
                  label={t("taxPersonalInfo.married")}
                  value="married"
                  checked={formData["married-single"] === "married"}
                  onChange={handleFormChange}
                  required
                />
              </div>
              <br />

              {formData["married-single"] === "married" && (
                <div>
                  <h3>{t("taxPersonalInfo.fileJointOrSeparate")}</h3>
                  <Radio
                    id="single1"
                    name="filing-status"
                    label={t("taxPersonalInfo.yes")}
                    value="MJ"
                    onChange={handleFormChange}
                    required
                  />
                  <Radio
                    id="joint"
                    name="filing-status"
                    label={t("taxPersonalInfo.no")}
                    value="MS"
                    onChange={handleFormChange}
                    required
                  />
                  <br />
                </div>
              )}

              {formData["married-single"] === "single" && (
                <div>
                  <h3>{t("taxPersonalInfo.headOfHouseholdQuestion")}</h3>
                  <Radio
                    id="yes"
                    name="filing-status"
                    label={t("taxPersonalInfo.yes")}
                    value="H"
                    onChange={handleFormChange}
                    required
                  />
                  <Radio
                    id="no"
                    name="filing-status"
                    label={t("taxPersonalInfo.no")}
                    value="S"
                    onChange={handleFormChange}
                    required
                  />
                </div>
              )}
            </Fieldset>
            <Button
              type="submit"
              className="bg-mint"
              style={{ borderRadius: "10px" }}
            >
              {t("taxPersonalInfo.submit")}
            </Button>
          </Form>
        </div>
      </GridContainer>
    </>
  );
}
