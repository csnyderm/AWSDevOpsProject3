import { Grid, GridContainer, SideNav } from "@trussworks/react-uswds";
import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import "../styles/NavbarWrapper.css";
import skillvestingLogo from "../assets/Skillvesting_icon.png";
import { useTranslation } from "react-i18next";

export default function LeftNav({ children }: { children: React.ReactNode }) {
  //This Component should wrap around another page or component, adding a navbar to the side
  //All children components are placed to the right of the navbar
  //Links can be added to the navbar by updating the array below
  //First string should be a path, second string should be a translation key. Sub-arrays will only show when the previous link is selected.
  const MIN_WINDOW_WIDTH = 768; // Define minimum window width for the side navigation

  const navList = [
    ["/", "sideNav.dashboard"],
    ["/account", "sideNav.account"],

    ["/goals", "sideNav.goals"],
    ["/budget", "sideNav.budget"],

    // ["/temp4", "Temp4"],
    // [["/test", "Array test"]],
    ["/tax-starter", "sideNav.taxStarter"],
    ["/investments", "sideNav.investments"],
  ];
  const displayOnPages = [
    "/",
    "/tax-personal-info",
    "/tax-income",
    "/tax-deductions",
    "/tax-overview",
  ];

  let allowedPages: Array<string> = [];

  const location = useLocation();
  const { t, i18n } = useTranslation();

  //For updating the sidenav at the bottom
  const [sideNavLinks, setSideNavlinks] = useState([<Link to={"/"}></Link>]);

  //Condition for allowing the sidenav to display
  const [displaySideNav, setDisplaySideNav] = useState(true);

  useEffect(() => {
    let generatedLinks = assembleNavElements(navList, location.pathname);
    generatedLinks.splice(
      0,
      0,
      <img
        src={skillvestingLogo}
        style={{
          width: "8em",
          display: "block",
          margin: "1em 1em",
          padding: "auto",
        }}
      ></img>
    );
    setSideNavlinks(generatedLinks);

    // Function to update the display of the side navigation
    const updateSideNavDisplay = () => {
      if (window.innerWidth < MIN_WINDOW_WIDTH) {
        setDisplaySideNav(false);
      } else {
        updateDisplaySideNav();
      }
    };

    // Update side nav display immediately
    updateSideNavDisplay();

    // Add event listener for window resizing
    window.addEventListener("resize", updateSideNavDisplay);

    // Cleanup: remove event listener when component unmounts
    return () => window.removeEventListener("resize", updateSideNavDisplay);
  }, [location, i18n.language]);

  //This function takes in the above navList array, along with the current page, and spits out a list of nav items that
  //  can be inputted into the parent SideNav in the return
  function assembleNavElements(
    formatArray: Array<Array<string | Array<string>>>,
    currentPage: string
  ): Array<React.ReactElement> {
    allowedPages = [];
    let assembledElements: Array<React.ReactElement> = [];
    formatArray.forEach((element) => {
      if (typeof element[0] == "string") {
        allowedPages.push(element[0]);

        let elementName = t(element[1]);

        if (element[0] != currentPage) {
          assembledElements.push(
            <Link
              style={{ color: "white" }}
              className="linkSideNavbar"
              to={element[0]}
            >
              {elementName}
            </Link>
          );
        } else {
          assembledElements.push(
            <Link
              style={{ color: "white" }}
              className="linkSideNavbar bg-gray-90"
              to={element[0]}
            >
              {elementName}
            </Link>
          );
        }
      } else {
        //type == object
        let lastElement = assembledElements.pop();
        if (typeof lastElement == undefined) {
          console.error("Navbar can not begin with sub-nav");
          return;
        } else {
          lastElement = lastElement as React.ReactElement;
        }
        let subNavSelected = false;

        if (lastElement?.props.className.includes("bg-gray-90")) {
          subNavSelected = true;
        }

        let subNavElements: Array<React.ReactElement> = [];
        element.forEach((subElement) => {
          allowedPages.push(subElement[0]);
          let elementName = t(subElement[1]);
          if (subElement[0] != currentPage) {
            subNavElements.push(
              <Link
                style={{ color: "white" }}
                className="linkSideNavbar"
                to={subElement[0]}
              >
                {elementName}
              </Link>
            );
          } else {
            subNavSelected = true;
            subNavElements.push(
              <Link
                style={{ color: "white" }}
                className="linkSideNavbar bg-gray-90"
                to={subElement[0]}
              >
                {elementName}
              </Link>
            );
          }
        });
        if (subNavSelected) {
          assembledElements.push(
            <>
              {lastElement}
              <SideNav isSubnav={true} items={subNavElements} />
            </>
          );
        } else {
          assembledElements.push(lastElement);
        }
      }
    });
    allowedPages = allowedPages.concat(displayOnPages);
    updateDisplaySideNav();
    return assembledElements;
  }

  //Sets whether or not the sidenav should show based on if the current path is located within the navbar
  function updateDisplaySideNav() {
    if (allowedPages.includes(location.pathname)) {
      setDisplaySideNav(true);
    } else {
      setDisplaySideNav(false);
    }
  }

  return (
    <>
      <GridContainer
        className="height-full width-full maxw-none padding-0 margin-0" /*containerSize="widescreen"*/
      >
        <Grid row className="height-full width-full">
          {displaySideNav && (
            <Grid
              className="bg-gray-70 position-sticky top-0 height-viewport flex-auto"
              col
            >
              <SideNav items={sideNavLinks} />
            </Grid>
          )}
          <Grid className="width-full" col>
            {children}
          </Grid>
        </Grid>
      </GridContainer>
    </>
  );
}
